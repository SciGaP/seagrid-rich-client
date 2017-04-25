/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/
package org.seagrid.desktop.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

public class SEAGridContext {
    private final static Logger logger = LoggerFactory.getLogger(SEAGridContext.class);

    private Map<String,String> dynamicConfigurations = new HashMap<>();

    private Properties properties = new Properties();
    private static final String PROPERTY_FILE_NAME = "/seagrid.properties";

    private static SEAGridContext instance;

    private UserPrefs userPrefs;

    private SEAGridContext(){
        InputStream inputStream = SEAGridContext.class.getResourceAsStream(PROPERTY_FILE_NAME);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SEAGridContext getInstance(){
        if(SEAGridContext.instance == null){
            SEAGridContext.instance = new SEAGridContext();
        }
        return SEAGridContext.instance;
    }

    public ZoneOffset getTimeZoneOffset(){
        LocalDateTime dt = LocalDateTime.now();
        return dt.atZone(TimeZone.getDefault().toZoneId()).getOffset();
    }

    public String getFileDownloadLocation(){
        if(properties.getProperty(SEAGridConfig.DEFAULT_FILE_DOWNLOAD_PATH) != null && !properties.getProperty(
                SEAGridConfig.DEFAULT_FILE_DOWNLOAD_PATH).isEmpty()) {
            return properties.getProperty(SEAGridConfig.DEFAULT_FILE_DOWNLOAD_PATH);
        }else{
            return System.getProperty("user.home") + File.separator + "SEAGrid" +  File.separator + "ExperimentData";
        }
    }

    public String getAiravataGatewayId(){
        if(SEAGridConfig.DEV){
            return properties.getProperty(SEAGridConfig.DEV_AIRAVATA_GATEWAY_ID);
        }
        return properties.getProperty(SEAGridConfig.AIRAVATA_GATEWAY_ID);
    }

    public void setUserName(String userName){ dynamicConfigurations.put(SEAGridConfig.USER_NAME, userName);}

    public String getUserName(){ return dynamicConfigurations.get(SEAGridConfig.USER_NAME);}

    public int getMaxRecentExpCount(){ return 20; }

    public String getRecentExperimentsDummyId(){ return "$$$$$$"; }

    public void setAuthenticated(boolean authenticated) {
        if(authenticated)
            dynamicConfigurations.put(SEAGridConfig.AUTHENTICATED, "true");
    }

    public boolean getAuthenticated() { return dynamicConfigurations.containsKey(SEAGridConfig.AUTHENTICATED); }

    public void setOAuthToken(String oauthToken) {
        dynamicConfigurations.put(SEAGridConfig.OAUTH_TOKEN,oauthToken);
    }

    public String getOAuthToken(){
        return dynamicConfigurations.get(SEAGridConfig.OAUTH_TOKEN);
    }

    public void setRefreshToken(String refreshToken){
        dynamicConfigurations.put(SEAGridConfig.OAUTH_REFRESH_TOKEN, refreshToken);
    }

    public String getRefreshToken(){
        return dynamicConfigurations.get(SEAGridConfig.OAUTH_REFRESH_TOKEN);
    }

    public void setTokenExpiaryTime(long tokenExpiarationTime) {
        dynamicConfigurations.put(SEAGridConfig.OAUTH_TOKEN_EXPIRATION_TIME, tokenExpiarationTime + "");
    }

    public long getOAuthTokenExpirationTime(){
        return Long.parseLong(dynamicConfigurations.get(SEAGridConfig.OAUTH_TOKEN_EXPIRATION_TIME));
    }

    public String getAiravataHost() {
        if(SEAGridConfig.DEV){
            return properties.getProperty(SEAGridConfig.DEV_AIRAVATA_HOST);
        }
        return properties.getProperty(SEAGridConfig.AIRAVATA_HOST);
    }

    public int getAiravataPort() {
        if(SEAGridConfig.DEV){
            return Integer.parseInt(properties.getProperty(SEAGridConfig.DEV_AIRAVATA_PORT));
        }
        return Integer.parseInt(properties.getProperty(SEAGridConfig.AIRAVATA_PORT));
    }

    public String getSFTPHost() {
        if(SEAGridConfig.DEV){
            return properties.getProperty(SEAGridConfig.DEV_SFTP_HOST);
        }
        return properties.getProperty(SEAGridConfig.SFTP_HOST);
    }

    public int getSFTPPort() {
        if(SEAGridConfig.DEV){
            return Integer.parseInt(properties.getProperty(SEAGridConfig.DEV_SFTP_PORT));
        }
        return Integer.parseInt(properties.getProperty(SEAGridConfig.SFTP_PORT));
    }

    public String getIdpUrl() {
        return properties.getProperty(SEAGridConfig.IDP_URL);
    }

    public String[] getAuthorisedUserRoles() {
        return properties.getProperty(SEAGridConfig.IDP_AUTHORISED_ROLES).split(",");
    }

    public String getOAuthClientId() {
        if(SEAGridConfig.DEV){
            return properties.getProperty(SEAGridConfig.DEV_IDP_OAUTH_CLIENT_ID);
        }else{
            return properties.getProperty(SEAGridConfig.IDP_OAUTH_CLIENT_ID);
        }
    }

    public String getOAuthClientSecret() {
        if(SEAGridConfig.DEV){
            return properties.getProperty(SEAGridConfig.DEV_IDP_OAUTH_CLIENT_SECRET);
        }else{
            return properties.getProperty(SEAGridConfig.IDP_OAUTH_CLIENT_SECRET);
        }
    }

    public String getIdpTenantId() {
        if(SEAGridConfig.DEV){
            return properties.getProperty(SEAGridConfig.DEV_IDP_TENANT_ID);
        }else {
            return properties.getProperty(SEAGridConfig.IDP_TENANT_ID);
        }
    }

    public CharSequence getGaussianAppName() {
        return "gaussian";
    }

    public CharSequence getGamessAppName() {
        return "gamess";
    }

    public String getGatewayaStorageId(){
        if(SEAGridConfig.DEV){
            return properties.getProperty(SEAGridConfig.DEV_GATEWAY_STORAGE_ID);
        }
        return properties.getProperty(SEAGridConfig.GATEWAY_STORAGE_ID);
    }

    public String getGatewayUserDataRoot(){
        if(SEAGridConfig.DEV){
            return properties.getProperty(SEAGridConfig.DEV_REMOTE_DATA_DIR_ROOT) + getUserName() + "/";
        }
        return properties.getProperty(SEAGridConfig.REMOTE_DATA_DIR_ROOT) + getUserName() + "/";
    }

    public String getRemoteDataDirPrefix(){
        if(SEAGridConfig.DEV){
            return properties.getProperty(SEAGridConfig.DEV_REMOTE_DATA_DIR_PREFIX);
        }
        return properties.getProperty(SEAGridConfig.REMOTE_DATA_DIR_PREFIX);
    }


    public UserPrefs getUserPrefs(){
        if(this.userPrefs == null){
            try
            {
                FileInputStream fis = new FileInputStream(getFileDownloadLocation()+"/.prefs/user_pref.ser");
                ObjectInputStream ois = new ObjectInputStream(fis);
                this.userPrefs = (UserPrefs)ois.readObject();
            }
            catch (Exception e){
                this.userPrefs = new UserPrefs();
            }
        }
        return this.userPrefs;
    }

    public void saveUserPrefs() {
        File file = new File((getFileDownloadLocation()+"/.prefs"));
        if(!file.exists()){
            file.mkdirs();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(getFileDownloadLocation()+"/.prefs/user_pref.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this.userPrefs);
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //FIXME - There is an issue in loading file from resources. This is a temporary hack
    public static final String logoBase64 = "iVBORw0KGgoAAAANSUhEUgAAAdkAAACTCAMAAAD1LZOOAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5" +
            "ccllPAAAAGxQTFRF1PPxWMW/j9jUx+zqLbqx8fr6LrevSsC6q+LfZc3H4/X0dM/KPLy11fHvgtTPuefkZsrFnd3as+vmnODc9Pz7YM7HNsC36f" +
            "n43vb0xu/sgNbRmOHcquXhOr63ctLMit3Wt+rmVsnBILKq////7wPFkQAAACR0Uk5T//////////////////////////////////////////////" +
            "8AWCwNDQAADaFJREFUeNrsnWmbpCgSgBMokUIO3ane7p7emdnV//8fN28vjgDBPJr45FOVovBCEBEEeBiKvKccShMUskUK2SKFbJFCtkghW6SQLWSLF" +
            "LJFCtkihWyRQrZIIVvIbpQv4/WX549Fnp/swXh98PyxSCFbpJAtZAvZQraQLWQL2UK2SCFbpJAtZKEiSYexQkdRGHdEvhrZipzenvYXQajFTMLv5uRa+aM" +
            "0GGv5JmSlQv1KkCIgsuTUJU7yJ77LePnfXarLVd0bhDYacnPXUkP1sXx1shUWvUUErpa//n44yr8OV/n598e/P24yXk0u/7n+8rt7vFmkAtWAoL4PqMLi5tZ6b8" +
            "2S9Ty5P9kK094h1Ngwt9K7470f99+OV5PLPwDv0NifD2hbiXqnUBbZKU79wjvmWT1qNmR+0vURiO9KlojeI6bKXUrn9RxiLFnmwuIdtbj3CrIWojbce2u+kexxlBM72Z" +
            "7KHckCmqXvlbl0Sfs0ZFvXw4lH5bSQGljatKrj7x2bb0q277GdLKCbJiPb9CBpTKVfwSYgizaQRbAaGPGAwLrQNv2a7LqxJu9I9iKLe6CodemV6FOR7VyPdk9OCloDYRguNfBey" +
            "p0PX5Bdq7j9yZIeLGRVetMnI+saO12qGrTxvaKvnQ9fku21lazeiayAt4tYlk76dGSPjXSSiy7D+NwQJ1e4I0Qmq8FqvMiAe002711prcgup9ORLN6HrNEkrVENqNth8rYpyJ6FXp" +
            "vpPEeA7ljXgJ4CaBi3yD9oTTbGOQTl79jzOWRFdgkQeQZ/crJLhHV3e0fOGmpTSF/nSMXPZVDif5+fn98+7zJe/roFNWCGVBDZhV1M1WSI6xW5+UiqVuTZbTqt9Kr62qEv1mQXgxb1KS" +
            "daANkFOuIOYMxL7+bDE/MEceNwsvNXbBZKkLcudawXXPmi+j7HT/Z2sgsNNyGL9iBL3I7NIGt7uxzQlCwlSVYEgslWvrlwrq2xwy9YG2py1m2Qw68wkG2tntnqLTnGOjFZ5rYc" +
            "JybCuu4HOiWrh8eQJe6uucTX2DW56eYZWuqYCQxkhd3nloanNGnJYm8Mj1h7/GHyj48m0SrezIKCRGuYPwoprOMOeW/G1snI5qRafo7sUWx96T51lYts47exGitZnoasvjUTA64EzGrQ+i" +
            "MZyFo35Vf2gWQrR5xsnNL53coLQBtGVge2y4zskIbsPVRXQU0NbJ1EjXYSstqP2t+xHWS1oUzpjIC27OS/42n5cLRhZIk/7Gcl+1cashW9z2etP6oIrQEBkSX+mLTjn8rwsPDYNhjtIWu" +
            "7TMl+piHLRp3P/GHFtGSrCLLtepZut5AFo01CVqJRVGay7ajZKlvYJ7wGfEzgwcxKdogg263meGYLNAPXo9COZM0ln2JQYwjq49styDTm0Ewv71dfAN+UTqZcma0GSchOw871sUPKx" +
            "mqSAclaDLn9yOYZs2xqgOse5OQ9lKxzNYJEkYVV4tXIopmZSY3Bgeciy8ArY2Cy+A3J8nmkpwG5tI8l6xi0LJJs94Zku/lEox3Bh6chS6CmEJQsLE3qxcjWC/+eQiKMDyZrS8pYJd" +
            "dsydV6dbJ8OT01EOX0aLKWpEs5xJEF5qy+Flm1tPolJAPh4WRNeRlUD3Fk1fBosj8i/VnX9g+x6rQC0IsfT/a8SWIeSZJDJNlqeMMxK9e+ggJ04ycgO1muOVeB+cqB53S/BVm1VkcSEGF8" +
            "BrLzgvAQTZa9JVlhsCCEPz/3rcjKdySrTYEb5VdRb0V2eEeyjck25H6zopB9drLUqI5q7+xTtPGTk9XmNPzOu2b5YmSvSol09e9iQTXmRuHerXgvRnb8p2k7evt4st8jIxU/LOVVtt2UtW9h62XJGn" +
            "cf8ixk9QPHLLPtZep8Lm1usnU2std8vqhBmyTfeBeyrW1pkvssi8xkeR9MVkHJmtbtWQ6yoQcoJCRb2XVR6+l0mcnicLIITNawbp9lFS90y25Cssy+sZT17qSZvGQ5zUqW5cupIHFGd2qytb1Olef" +
            "NspLlNTBjNJJsZczGICnIDs4NpHuR5S7DsHVbFh6yXJ+yjDWPIrtYnlPJyVrOPxGKEbKV7DIjoNUPINu5dvkztzvgIsvHeIDAPJCsVBQeIYol6zgAheptZNcZWrADKH/M/VnruYt/Ljzbr0B" +
            "lfPMNumCyfBHmoTiArFQCeLjMNrKxZyVBFs9M8UwY3NlePNsJJBjwItxdkcbZrDayhvSk9Rl7ZrK8E6Ghv1iyJDKnfMN5UAC4ycgq95DQTlVoIwvaD2UgWzEUseMmlqyMzCkPSCw" +
            "zzuNyH7LCY+1Tl/kSQnZliC3JVqyNSxeNJTvkJOtaX6oZz09W+maVxuXSBpFdatQ5Wd1EJ7I8J1n3mZJ1xzOTVb6IqXaFtcPICkdQkG7IUHpOst7DBxuSlazwNh11RBjDyC6ma+he" +
            "G+GL+T0rWe/R1UjmI6v9mbbK8YtAsjiGLPZG1J+W7DAwEbxDLBHZxr98JR2jOpBsG0yWKkBg7onJHsdOG5jknIgsBcxjwu4WBZJFwWQFpPGemqzT5je5HWnIApTxTR3zh5AFZSTEkuX7k" +
            "PXAZfPgYqJ9PQBlfFPHOJgswozMA0oRZJuMZEnkHumoT1tZ4S63hCZZEahg64fCqhetZIW6DrZKhJN1H7eYjizLGF0MgIsykGWwPWjYWlUbWWn+CYSsUJwFquMMaz3OHJcNn6OrWO3rREn" +
            "ItrDELm7Vi4CVdx1C9jLUq0B1HEsW5VvFc7XmKtu5SU4WqIyvC32mXgwgS+Bk70GZNixBLGVORU8bRiq3q7X5E5KLz4rR5GQ76IbgztYDUpJVlXGcAzpewjwoigGZhgk+DtrY1XEKsjVU3XGb" +
            "1k5HlpK1mw3NAo4ka5jxalDCUorPvrZWDysBWQ73GGuLS5uOrLZ36UzRRQJYQs5HVuYk24HdiutPu3xkkd3s8qvjKLKVCLKHU5OdaYxRLX2Pi1QsNvaAlfHN2BD5yGpj1BOQTwH7MWxfDxl2J" +
            "As6kzxuzMqQDUWtOWkmGVnpsjB8kx8KJ8vrwIyc5GS7fGQVXBnf7EiVjaxzGupSk+Uq8/5ZiOeTj6wI2SlWmbtBNrKzTTd1KrLofH52a1kzrd6E7HVUCAQTalTd+ciqAHUMJrt9wfAVyMK/EO" +
            "rwLfORlQG72dKQRe9ClsaQXWqsfGRn6lgUsnCyOgrs0srISBYDN/UsXMN4svRNyDZxZOvdyHLgRrxBC/82SdCpQfwJyCaIVMQp42X1M5KdjUS7Oq5af2YRkGz3FmM2Uhkvmy0n2Q4SHjIm4q/D" +
            "vyCy4i3ItrFkxW5kOWT9vQaZ8MDzjdkbkK36aNF7kZ1Ro/7gq8tNgpGl8vXJsniyzW5k/elQBJqfBvyOwI6reLnItvFkZ0tdWcn606EQ1DmFftWlfnWyG5TxfDbKStabDkXASaV7fz2NTeKyck+y" +
            "3RayaDeyzGPezHxy1CC7EfWE38XLQ7beQnbq0uYlW/XudCi6SGDira3EJ/zGlnF9duvuD74J7NSjx36fYQPZuTlQORYNbqZPa2lQlHLIpjkrFUPGbOh33rttZIX59XBA3wSSdadDkTURbnkdFG5CJ" +
            "COr/J54m4rsNmU8jc9jv13ZbiDrznDqDH0NbSELPfoyjKxwLY97chc/WBBZuRHsxAMhXlUm+y1knelQ2FAu3kA24ZeYmK/DzKOidrL3iR9EVm0lS81kTX7+vAYqlKx2Rex1PFmBSTVU8+8JwE+Y3n" +
            "5W6iLcPRsTh8n/Pu5VA5EVW8lOjAJPhr2sHaspALIzdVxbmy+QLB2betxhE3AK8eazUpenV8yGxEFNyd4aFUJ2szKezvi1a1cMb6wTNJSsSx1XTrLaSnauW7rAxEUY2dV2u/F15Or4wflMfNAzssd7G" +
            "SHkP+Qu4+XPxMp42smWhZ13sp2pkq71ZC1AyBLXhCVcZLmN7HLSYAFrAWCyhsB8fQlJudzI7xcn9S//WakX+WfuztIEZLshxjXG4WSd6VDKQVbYIhViZQ1w3IUd9Q8gWwVMeYvwyGFi9FtPILnIH2nW3" +
            "M2R8ya6BjCyypEOxR1kOxvZ0I+sRK4I6Bir5Va6iCLbpCA7tnEFVwFsiCArXXkwjZXsohehwNWcBGs94HZu1qWTKLI0CVkVbpA1QwxZpzq+fUJiTbazreJ1e5GNXhM+TKoRQpYlATttYxYJFkjWpY5vz16" +
            "RRdZWlruRhY3atjKW3oSTbdOQnU4OkgYN8kCy7nQoZSS7GgcI8KDkZNefoAcFNA+TioSQTQR2vsnY212EHmLJetKhlIHsOmLyGLLzZUXTUje3l34+Z/URY3be2YhzUrEc6wEk27nToTRdkl1/sWAkS3cle9Rn" +
            "DpVsO994EqkKIctRErArfUesXUbYvEUgWV92atXMyArmsmaanckeX68zH9xmOpP8a7HGfvj19+dVvn1+ri9/3X53ZaCuWyZvURGgNKe9p5dLZVKuFWtWy4Oi7ewmC56Ic7qa/M5o2vLxdQhz2ql1tTvZ88jVWI0" +
            "NqbCWwNIPodky+YQTcoPg+1LVnsIur6TTlJa3QZ+U7G8hhWwhW8gWsoVsIVvIFrJFCtkihWwhW8gWsoXs70L2y3j95fljkecnW6SQLVLIFilkC9kihWyRQrZIIVukkC0SIv8XYADBmU9ntNClogAAAABJRU5ErkJggg==";
}