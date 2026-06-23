<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { toast } from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';
import { useSwalLoading } from '@/composables/useSwalLoading';

definePageMeta({ layout: 'blank' });

const route = useRoute();
const { showLoading, hideLoading } = useSwalLoading();

const token = ref('');
const account = ref('');
const password = ref('');
const confirmPassword = ref('');
const valid = ref(false);
const completed = ref(false);
const message = ref('');

const invalidMessage = '此更改密碼連結已失效，如需重新更改，請管理者點選忘記密碼按鈕。';

const validateToken = async () => {
  token.value = String(route.query.token || '');
  if (!token.value) {
    valid.value = false;
    message.value = invalidMessage;
    return;
  }

  showLoading();
  try {
    const response: any = await useApi('/auth/passwordReset/validate', {
      method: 'POST',
      body: { token: token.value }
    });
    hideLoading();
    if (response && response.success == import.meta.env.VITE_SUCCESS_FLAG && response.value?.valid) {
      valid.value = true;
      account.value = response.value.account || '';
      message.value = '';
    } else {
      valid.value = false;
      message.value = response?.value?.message || response?.message || invalidMessage;
    }
  } catch (e: any) {
    hideLoading();
    valid.value = false;
    message.value = e.response?._data?.message || invalidMessage;
  }
};

const submitPassword = async () => {
  if (!password.value || !confirmPassword.value) {
    toast.warning('請輸入密碼');
    return;
  }
  if (password.value !== confirmPassword.value) {
    toast.warning('密碼確認不一致');
    return;
  }
  showLoading();
  try {
    const response: any = await useApi('/auth/passwordReset/complete', {
      method: 'POST',
      body: {
        token: token.value,
        password: password.value,
        confirmPassword: confirmPassword.value
      }
    });
    hideLoading();
    if (response && response.success == import.meta.env.VITE_SUCCESS_FLAG) {
      completed.value = true;
      valid.value = false;
      message.value = '密碼已更新';
      toast.success(response.message || '密碼已更新');
    } else {
      message.value = response?.message || invalidMessage;
      toast.warning(message.value);
    }
  } catch (e: any) {
    hideLoading();
    message.value = e.response?._data?.message || invalidMessage;
    toast.warning(message.value);
  }
};

onMounted(() => {
  validateToken();
});
</script>

<template>
  <div class="login-body">
    <section class="login-content">
      <div class="login-box">
        <div class="login-form">
          <h3 class="login-head">
            <i class="bi bi-key"></i>
            MindScore
          </h3>

          <div v-if="valid">
            <div class="mb-3">
              <label class="form-label">帳號</label>
              <input type="text" class="form-control" :value="account" readonly>
            </div>
            <div class="mb-3">
              <label for="password" class="form-label">新密碼</label>
              <input id="password" type="password" class="form-control" v-model="password" autocomplete="new-password">
            </div>
            <div class="mb-3">
              <label for="confirmPassword" class="form-label">確認密碼</label>
              <input id="confirmPassword" type="password" class="form-control" v-model="confirmPassword" autocomplete="new-password" @keyup.enter="submitPassword">
            </div>
            <button type="button" class="btn btn-primary col-12" @click="submitPassword">
              <i class="bi bi-check2-circle"></i>
              送出
            </button>
          </div>

          <div v-else>
            <div :class="['alert', completed ? 'alert-success' : 'alert-warning']">{{ message }}</div>
            <NuxtLink class="btn btn-outline-primary col-12" to="/login">回登入頁</NuxtLink>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>
