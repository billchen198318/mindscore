package org.qifu.md.logic.impl;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.qifu.base.Constants;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.message.BaseSystemMessage;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.ServiceAuthority;
import org.qifu.base.model.ServiceMethodAuthority;
import org.qifu.base.model.ServiceMethodType;
import org.qifu.base.model.YesNoKeyProvide;
import org.qifu.core.entity.TbAccount;
import org.qifu.core.entity.TbSysMailHelper;
import org.qifu.core.entity.TbUserRole;
import org.qifu.core.service.IAccountService;
import org.qifu.core.service.ISysMailHelperService;
import org.qifu.core.service.IUserRoleService;
import org.qifu.core.util.SystemSettingConfigureUtils;
import org.qifu.md.entity.MdOrgMember;
import org.qifu.md.entity.MdPasswordResetToken;
import org.qifu.md.logic.IMdOrgMemberLogicService;
import org.qifu.md.mapper.MdOrgMemberMapper;
import org.qifu.md.mapper.MdPasswordResetTokenMapper;
import org.qifu.md.model.PasswordResetRequest;
import org.qifu.md.model.PasswordResetTokenStatus;
import org.qifu.md.service.IMdOrgMemberService;
import org.qifu.md.service.IMdPasswordResetTokenService;
import org.qifu.util.SimpleUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@ServiceAuthority(check = true)
@Transactional(propagation = Propagation.REQUIRED, timeout = 300, readOnly = false)
public class MdOrgMemberLogicServiceImpl implements IMdOrgMemberLogicService {
    private static final long PASSWORD_RESET_TOKEN_TTL_MILLIS = 35L * 60L * 1000L;
    private static final int PASSWORD_RESET_TOKEN_BYTES = 32;

    private final IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService;
    private final IMdPasswordResetTokenService<MdPasswordResetToken, String> mdPasswordResetTokenService;
    private final IAccountService<TbAccount, String> accountService;
    private final IUserRoleService<TbUserRole, String> userRoleService;
    private final ISysMailHelperService<TbSysMailHelper, String> sysMailHelperService;
    private final MdOrgMemberMapper mdOrgMemberMapper;
    private final MdPasswordResetTokenMapper mdPasswordResetTokenMapper;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${mindscore.password-reset.frontend-base-url:http://localhost:8077}")
    private String passwordResetFrontendBaseUrl;

    @Value("${spring.mail.username:no-reply@localhost}")
    private String mailFrom;

    public MdOrgMemberLogicServiceImpl(IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService,
            IMdPasswordResetTokenService<MdPasswordResetToken, String> mdPasswordResetTokenService,
            IAccountService<TbAccount, String> accountService,
            IUserRoleService<TbUserRole, String> userRoleService,
            ISysMailHelperService<TbSysMailHelper, String> sysMailHelperService,
            MdOrgMemberMapper mdOrgMemberMapper,
            MdPasswordResetTokenMapper mdPasswordResetTokenMapper,
            PasswordEncoder passwordEncoder) {
        this.mdOrgMemberService = mdOrgMemberService;
        this.mdPasswordResetTokenService = mdPasswordResetTokenService;
        this.accountService = accountService;
        this.userRoleService = userRoleService;
        this.sysMailHelperService = sysMailHelperService;
        this.mdOrgMemberMapper = mdOrgMemberMapper;
        this.mdPasswordResetTokenMapper = mdPasswordResetTokenMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @ServiceMethodAuthority(type = {ServiceMethodType.INSERT})
    @Transactional(
            propagation = Propagation.REQUIRED,
            readOnly = false,
            rollbackFor = {RuntimeException.class, IOException.class, Exception.class})
    @Override
    public void createMemberWithAccount(MdOrgMember entity) throws ServiceException {
        TbAccount account = new TbAccount();
        account.setAccount(entity.getAccount());
        if (this.accountService.countByUK(account) > 0) {
            throw new ServiceException("Account already exists: " + entity.getAccount());
        }
        account.setOid(SimpleUtils.getUUIDStr());
        account.setPassword(this.passwordEncoder.encode(generateTokenValue()));
        account.setOnJob(YesNoKeyProvide.YES);
        account.setCuserid(entity.getCuserid());
        account.setCdate(new Date());
        this.accountService.insert(account);

        TbUserRole userRole = new TbUserRole();
        userRole.setRole("COMMON01");
        userRole.setAccount(account.getAccount());
        userRole.setDescription("Auto-created for org member");
        this.userRoleService.insert(userRole);

        this.mdOrgMemberService.insert(entity);
        this.createPasswordResetMail(entity);
    }

    @ServiceMethodAuthority(type = {ServiceMethodType.UPDATE})
    @Transactional(
            propagation = Propagation.REQUIRED,
            readOnly = false,
            rollbackFor = {RuntimeException.class, IOException.class, Exception.class})
    @Override
    public DefaultResult<Boolean> sendPasswordResetMail(MdOrgMember entity) throws ServiceException {
        MdOrgMember member = loadEnabledMember(entity);
        this.createPasswordResetMail(member);

        DefaultResult<Boolean> result = new DefaultResult<>();
        result.setSuccess(YesNoKeyProvide.YES);
        result.setValue(true);
        result.setMessage("Password reset mail has been queued.");
        return result;
    }

    @Override
    public DefaultResult<PasswordResetTokenStatus> validatePasswordResetToken(PasswordResetRequest request) throws ServiceException {
        PasswordResetTokenStatus status = new PasswordResetTokenStatus();
        MdPasswordResetToken token = findActiveToken(request == null ? null : request.getToken());
        status.setValid(token != null);
        if (token != null) {
            status.setAccount(token.getAccount());
            status.setMessage("OK");
        } else {
            status.setMessage("此更改密碼連結已失效，如需重新更改，請管理者點選忘記密碼按鈕。");
        }

        DefaultResult<PasswordResetTokenStatus> result = new DefaultResult<>();
        result.setSuccess(YesNoKeyProvide.YES);
        result.setValue(status);
        return result;
    }

    @Transactional(
            propagation = Propagation.REQUIRED,
            readOnly = false,
            rollbackFor = {RuntimeException.class, IOException.class, Exception.class})
    @Override
    public DefaultResult<Boolean> resetPasswordByToken(PasswordResetRequest request) throws ServiceException {
        if (request == null || StringUtils.isBlank(request.getPassword()) || StringUtils.isBlank(request.getConfirmPassword())) {
            throw new ServiceException(BaseSystemMessage.parameterBlank());
        }
        if (!Strings.CS.equals(request.getPassword(), request.getConfirmPassword())) {
            throw new ServiceException("Password and confirm password do not match.");
        }
        if (request.getPassword().length() < 8) {
            throw new ServiceException("Password length must be at least 8 characters.");
        }
        MdPasswordResetToken token = findActiveToken(request.getToken());
        if (token == null) {
            throw new ServiceException("此更改密碼連結已失效，如需重新更改，請管理者點選忘記密碼按鈕。");
        }

        TbAccount account = new TbAccount();
        account.setAccount(token.getAccount());
        account = this.accountService.selectByUniqueKey(account).getValueEmptyThrowMessage();
        account.setPassword(this.passwordEncoder.encode(request.getPassword()));
        account.setOnJob(YesNoKeyProvide.YES);
        this.accountService.update(account).getValueEmptyThrowMessage();

        token.setUsedFlag(YesNoKeyProvide.YES);
        token.setUsedTime(new Date());
        this.mdPasswordResetTokenService.update(token).getValueEmptyThrowMessage();

        DefaultResult<Boolean> result = new DefaultResult<>();
        result.setSuccess(YesNoKeyProvide.YES);
        result.setValue(true);
        result.setMessage(BaseSystemMessage.updateSuccess());
        return result;
    }

    @ServiceMethodAuthority(type = {ServiceMethodType.DELETE})
    @Transactional(
            propagation = Propagation.REQUIRED,
            readOnly = false,
            rollbackFor = {RuntimeException.class, IOException.class, Exception.class})
    @Override
    public DefaultResult<Boolean> deleteMemberWithAccount(MdOrgMember entity) throws ServiceException {
        if (entity == null || StringUtils.isBlank(entity.getOid())) {
            throw new ServiceException(BaseSystemMessage.parameterBlank());
        }

        MdOrgMember member = this.mdOrgMemberService.selectByEntityPrimaryKey(entity).getValueEmptyThrowMessage();
        assertNoBusinessReference(member);

        member.setEnabled(YesNoKeyProvide.NO);
        this.mdOrgMemberService.update(member).getValueEmptyThrowMessage();

        TbAccount account = new TbAccount();
        account.setAccount(member.getAccount());
        account = this.accountService.selectByUniqueKey(account).getValueEmptyThrowMessage();
        account.setOnJob(YesNoKeyProvide.NO);
        this.accountService.update(account).getValueEmptyThrowMessage();

        DefaultResult<Boolean> result = new DefaultResult<>();
        result.setSuccess(YesNoKeyProvide.YES);
        result.setValue(true);
        result.setMessage(BaseSystemMessage.deleteSuccess());
        return result;
    }

    private void assertNoBusinessReference(MdOrgMember member) throws ServiceException {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("oid", member.getOid());
        paramMap.put("account", member.getAccount());
        List<String> referenceNames = this.mdOrgMemberMapper.findBusinessReferenceNamesByAccount(paramMap);
        if (referenceNames != null && !referenceNames.isEmpty()) {
            throw new ServiceException("成員帳號已有資料使用，不允許刪除：" + String.join(", ", referenceNames));
        }
    }

    private MdOrgMember loadEnabledMember(MdOrgMember entity) throws ServiceException {
        if (entity == null || StringUtils.isBlank(entity.getOid())) {
            throw new ServiceException(BaseSystemMessage.parameterBlank());
        }
        MdOrgMember member = this.mdOrgMemberService.selectByEntityPrimaryKey(entity).getValueEmptyThrowMessage();
        if (YesNoKeyProvide.NO.equals(member.getEnabled())) {
            throw new ServiceException("Member is disabled.");
        }
        return member;
    }

    private void createPasswordResetMail(MdOrgMember member) throws ServiceException {
        if (member == null || StringUtils.isBlank(member.getAccount()) || StringUtils.isBlank(member.getEmail())) {
            throw new ServiceException(BaseSystemMessage.parameterBlank());
        }
        this.revokeActiveTokens(member.getAccount());

        String tokenValue = generateTokenValue();
        MdPasswordResetToken token = new MdPasswordResetToken();
        token.setAccount(member.getAccount());
        token.setTokenHash(hashToken(tokenValue));
        token.setExpiresTime(new Date(System.currentTimeMillis() + PASSWORD_RESET_TOKEN_TTL_MILLIS));
        token.setUsedFlag(YesNoKeyProvide.NO);
        token.setRevokedFlag(YesNoKeyProvide.NO);
        this.mdPasswordResetTokenService.insert(token).getValueEmptyThrowMessage();

        TbSysMailHelper mail = new TbSysMailHelper();
        String today = new java.text.SimpleDateFormat("yyyyMMdd").format(new Date());
        mail.setMailId(this.sysMailHelperService.findForMaxMailIdComplete(today));
        mail.setSubject("MindScore password setup");
        mail.setText(buildPasswordResetMailText(member, tokenValue).getBytes(Charset.forName(Constants.BASE_ENCODING)));
        mail.setMailFrom(StringUtils.defaultIfBlank(SystemSettingConfigureUtils.getMailDefaultFromValue(), this.mailFrom));
        mail.setMailTo(member.getEmail());
        mail.setSuccessFlag(YesNoKeyProvide.NO);
        mail.setRetainFlag(YesNoKeyProvide.NO);
        this.sysMailHelperService.insert(mail).getValueEmptyThrowMessage();
    }

    private void revokeActiveTokens(String account) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("account", account);
        this.mdPasswordResetTokenMapper.revokeActiveTokensByAccount(paramMap);
    }

    private MdPasswordResetToken findActiveToken(String tokenValue) throws ServiceException {
        if (StringUtils.isBlank(tokenValue)) {
            return null;
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("tokenHash", hashToken(tokenValue));
        return this.mdPasswordResetTokenMapper.selectActiveByTokenHash(paramMap);
    }

    private String generateTokenValue() {
        byte[] bytes = new byte[PASSWORD_RESET_TOKEN_BYTES];
        this.secureRandom.nextBytes(bytes);
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String tokenValue) throws ServiceException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(tokenValue.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new ServiceException("Unable to hash password reset token.");
        }
    }

    private String buildPasswordResetMailText(MdOrgMember member, String tokenValue) {
        String resetUrl = Strings.CS.removeEnd(this.passwordResetFrontendBaseUrl, "/")
                + "/password-reset?token="
                + URLEncoder.encode(tokenValue, StandardCharsets.UTF_8);
        String displayName = StringUtils.defaultIfBlank(member.getDisplayName(), member.getAccount());
        return "Hello " + displayName + ",\n\n"
                + "Please use the following link to set your MindScore password within 1 hour:\n"
                + resetUrl + "\n\n"
                + "If the link has expired, please contact the administrator to send a new password reset mail.";
    }
}
