package com.example.passwordkeeper;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.R;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static com.example.passwordkeeper.Constants.PREFS_NAME;

public class LoginActivity extends Activity implements View.OnClickListener {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private EditText etPasswordEntered;
    static String oldPass;
    private ImageView ivFingerprint; // TODO maybe use this?
    private static final String KEY_NAME = "my_key";
    private KeyStore keyStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences passwords = getSharedPreferences(PREFS_NAME, 0);
        oldPass = passwords.getString("password", "g");
        // returns g if the passowrd does not exist
        userFingerPrint();
    }

    private void userFingerPrint() {
        final KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        final FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        if (keyguardManager == null || fingerprintManager == null) {
            return;
        }

        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(getCrypto(), cancellationSignal, 0, mAuthenticationCallback, null);
    }

    private FingerprintManager.CryptoObject getCrypto() {
        return new FingerprintManager.CryptoObject(getCipher());
    }

    private Cipher getCipher() {
        Cipher cipher = null;

        generateKey();

        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" +
                    KeyProperties.BLOCK_MODE_CBC + "/" +
                    KeyProperties.ENCRYPTION_PADDING_PKCS7);

            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            //Return true if the cipher has been initialized successfully//
//            return true;
        } catch (NoSuchPaddingException | NoSuchAlgorithmException ex) {
            Log.e(TAG, "Received Exception when getting Cipher : " + ex);
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }

        return cipher;
    }

    private void generateKey() /*throws FingerprintException */{
        try {
            // Obtain a reference to the Keystore using the standard Android keystore container identifier (“AndroidKeystore”)//
            keyStore = KeyStore.getInstance("AndroidKeyStore");

            //Generate the key//
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            //Initialize an empty KeyStore//
            keyStore.load(null);

            //Initialize the KeyGenerator//
            keyGenerator.init(new

                    //Specify the operation(s) this key can be used for//
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)

                    //Configure this key so that the user has to confirm their identity with a fingerprint each time they want to use it//
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());

            //Generate the key//
            keyGenerator.generateKey();

        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            exc.printStackTrace();
//            throw new FingerprintException(exc);
        }
    }

    private final FingerprintManager.AuthenticationCallback mAuthenticationCallback = new FingerprintManager.AuthenticationCallback() {
        /**
         * Called when an unrecoverable error has been encountered and the operation is complete.
         * No further callbacks will be made on this object.
         *
         * @param errorCode An integer identifying the error message
         * @param errString A human-readable error string that can be shown in UI
         */
        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
            Log.e(TAG, "onAuthenticationError + errorCode: " + errorCode + " errString: " + errString);
        }

        /**
         * Called when a recoverable error has been encountered during authentication. The help
         * string is provided to give the user guidance for what went wrong, such as
         * "Sensor dirty, please clean it."
         *
         * @param helpCode   An integer identifying the error message
         * @param helpString A human-readable string that can be shown in UI
         */
        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            super.onAuthenticationHelp(helpCode, helpString);
            Log.e(TAG, "onAuthenticationHelp + result: " + helpString.toString());
        }

        /**
         * Called when a fingerprint is recognized.
         *
         * @param result An object containing authentication-related data
         */
        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            Log.e(TAG, "onAuthenticationSucceeded + result: " + result.toString());
            loginWithFingerprint();
        }

        /**
         * Called when a fingerprint is valid but not recognized.
         */
        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            Log.e(TAG, "authenticationFailed");
        }
    };


//    private void passwordMAIN() {
//
//        https://www.androidauthority.com/how-to-add-fingerprint-authentication-to-your-android-app-747304/
//
//
//        // If you’ve set your app’s minSdkVersion to anything lower than 23, then you’ll need to verify that the device is running Marshmallow
//        // or higher before executing any fingerprint-related code
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            //Get an instance of KeyguardManager and FingerprintManager//
//            KeyguardManager keyguardManager =
//                    (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
//            FingerprintManager fingerprintManager =
//                    (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
//
////            textView = (TextView) findViewById(R.id.textview);
//
//            //Check whether the device has a fingerprint sensor//
//            if (!fingerprintManager.isHardwareDetected()) { //TODO
//                // If a fingerprint sensor isn’t available, then inform the user that they’ll be unable to use your app’s fingerprint functionality//
////                textView.setText("Your device doesn't support fingerprint authentication");
//            }
//            //Check whether the user has granted your app the USE_FINGERPRINT permission//
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) { // TODO
//                // If your app doesn't have this permission, then display the following text//
////                textView.setText("Please enable the fingerprint permission");
//            }
//
//            //Check that the user has registered at least one fingerprint//
//            if (!fingerprintManager.hasEnrolledFingerprints()) { //TODO
//                // If the user hasn’t configured any fingerprints, then display the following message//
////                textView.setText("No fingerprint configured. Please register at least one fingerprint in your device's Settings");
//            }
//
//            //Check that the lockscreen is secured//
//            if (!keyguardManager.isKeyguardSecure()) { //TODO
//                // If the user hasn’t secured their lockscreen with a PIN password or pattern, then display the following text//
////                textView.setText("Please enable lockscreen security in your device's Settings");
//            } else {
//                generateKey(); //TODO
//            } catch (FingerprintException e) {
//                e.printStackTrace();
//            }
//
//            if (initCipher()) { //TODO
//            if (initCipher()) { //TODO
//                //If the cipher is initialized successfully, then create a CryptoObject instance//
//                cryptoObject = new FingerprintManager.CryptoObject(cipher);
//
//                // Here, I’m referencing the FingerprintHandler class that we’ll create in the next section. This class will be responsible
//                // for starting the authentication process (via the startAuth method) and processing the authentication process events//
//                FingerprintHandler helper = new FingerprintHandler(this);
//                helper.startAuth(fingerprintManager, cryptoObject);
//            }
//        }
//
//    }
//
//    private void checkForFingerprintPermissions() {
//
//    }


    public void login(View v) {
        etPasswordEntered = (EditText) findViewById(R.id.et_password);
        String password = etPasswordEntered.getText().toString();
        final Intent intent = PasswordListActivity.getIntent(this, password);

        if (password.equals(oldPass)) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Invalid Password", Toast.LENGTH_SHORT).show();
        }
    }

    private void loginWithFingerprint() {
        final Intent intent = PasswordListActivity.getIntent(this, oldPass);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
    }

//    public double[] listOfValues;
//    public void getMedianValue(final double[] values) {

//    }

}
