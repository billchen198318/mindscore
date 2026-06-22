package org.qifu.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@SpringBootTest(
        classes = DatabasePasswordJasyptTest.TestApplication.class,
        properties = {
                "jasypt.encryptor.algorithm=PBEWithMD5AndDES",
                "jasypt.encryptor.iv-generator-classname=org.jasypt.iv.NoIvGenerator",
                "jasypt.encryptor.password=mindscore-test-key",
                "test.datasource.password=ENC(Wao/IrU6W1gy6/m0cWVHyVk6ssdLxOvW)"
        }
)
public class DatabasePasswordJasyptTest {

    private static final String ALGORITHM = "PBEWithMD5AndDES";

    @Autowired
    private Environment environment;

    @Test
    public void testEncryptAndDecryptDatabasePassword() {
        String rawPassword = "P@ssw0rd";
        String encryptorPassword = "mindscore-dev-jasypt-key";

        StandardPBEStringEncryptor encryptor = buildEncryptor(encryptorPassword);
        String cipherText = encryptor.encrypt(rawPassword);
        
        System.out.println("cipherText>>>" + cipherText);
        
        String encryptedPropertyValue = "ENC(" + cipherText + ")";

        assertNotEquals(rawPassword, cipherText);
        assertTrue(encryptedPropertyValue.startsWith("ENC("));
        assertTrue(encryptedPropertyValue.endsWith(")"));
        assertEquals(rawPassword, encryptor.decrypt(cipherText));
        
        System.out.println("cipherText decrypt>>>" + encryptor.decrypt(cipherText));
    }

    @Test
    public void testEncryptedValueCanBeRenderedForPropertiesFile() {
        String rawPassword = "password";
        String encryptorPassword = "mindscore-test-key";
        StandardPBEStringEncryptor encryptor = buildEncryptor(encryptorPassword);

        String propertyValue = "ENC(" + encryptor.encrypt(rawPassword) + ")";

        assertTrue(propertyValue.matches("^ENC\\(.+\\)$"));
    }

    @Test
    public void testJasyptSpringBootStarterDecryptsEnvironmentProperty() {
        assertEquals("password", environment.getProperty("test.datasource.password"));
    }

    private StandardPBEStringEncryptor buildEncryptor(String encryptorPassword) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm(ALGORITHM);
        encryptor.setPassword(encryptorPassword);
        return encryptor;
    }

    @SpringBootConfiguration
    @EnableEncryptableProperties
    static class TestApplication {
    }
}
