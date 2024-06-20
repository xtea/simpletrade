package io.xtea;

import com.tigerbrokers.stock.openapi.client.config.ClientConfig;
import com.tigerbrokers.stock.openapi.client.https.client.TigerHttpClient;
import com.tigerbrokers.stock.openapi.client.util.ApiLogger;

import org.springframework.context.annotation.Configuration;

import java.net.URL;

/**
 * TODO: doc this.
 *
 * @author xtea
 * @date 2023-03-16 21:39
 */
@Configuration
public class AppConfig {

    public static ClientConfig clientConfig = ClientConfig.DEFAULT_CONFIG;

    static {
        // 开启日志. log file name: tiger_openapi.2023-02-22.log
        ApiLogger.setEnabled(true, "/tmp/logs/");
        // ApiLogger.setDebugEnabled(false);        // 开启debug级别日志
        // The tiger_openapi_config.properties file is stored in your local directory.
        URL rootPath = AppConfig.class.getClassLoader().getResource("./");

        clientConfig.configFilePath = rootPath.getPath();
        // clientConfig.isSslSocket = true;         // default is true
        // clientConfig.isAutoGrabPermission = true;// default is true
        // clientConfig.failRetryCounts = 2;        // fail retry count, default is 2
        // clientConfig.timeZone = TimeZoneId.Shanghai; // default time zone
        // clientConfig.language = Language.en_US;  // default language
        // clientConfig.isAutoRefreshToken = true;  // default is true, only support 'TBHK' license
        // clientConfig.secretKey = "xxxxxx";// 机构用户私钥

        // 原来旧的使用方式（不使用tiger_openapi_config.properties文件），必须配置tigerId, defaultAccount, privateKey三项，如果同时配置了configFilePath路径properties文件配置内容优先
        // clientConfig.tigerId = "your tiger id";
        // clientConfig.defaultAccount = "your account";
        // clientConfig.privateKey = FileUtil.readPrivateKey("/Users/tiger/rsa_private_key_pkcs8.pem");
    }

    public static TigerHttpClient client = TigerHttpClient.getInstance().clientConfig(clientConfig);

}