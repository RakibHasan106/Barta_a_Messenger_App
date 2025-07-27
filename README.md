## Baarta - A Messenger App
A chat application where an user can chat with another user, send them pictures , documents etc privately and securely . User will have to think less about security and data leakage as the copies of their messages will be deleted from the server once the receiver receives the message.

## Features
- **🔒 Secure End-to-End Messaging**  
All messages are encrypted to ensure privacy and secure communication between users.

- **🕒 Self-Destructing Messages**  
  Messages are automatically deleted from the server once the receiver reads them, minimizing data retention risks.

- **📷 Media Sharing**  
  Share pictures, documents, and other media files privately and securely.

- **✅ User Authentication**  
  Robust authentication mechanisms ensure only authorized users can access the application.

- **⚡ Real-Time Messaging**  
  Enjoy seamless real-time messaging with instant notifications for new messages.

- **📶 Offline Messaging**  
  Messages are stored locally and automatically sent when the user comes back online.

- **🚫 No Message Logs**  
  The server does not retain logs of messages after delivery, ensuring user privacy.

- **✨ User-Friendly Interface**  
  Intuitive and easy-to-navigate design for a smooth user experience.

<h2>🔐 End-to-End Encryption</h2>

<p><strong>Barta: A Messenger App</strong> ensures private communication using a hybrid <strong>End-to-End Encryption</strong> system combining AES and RSA encryption.</p>

<h3>🔧 Key Concepts</h3>
<ul>
  <li><strong>Hybrid Encryption:</strong> Combines the speed of AES with the security of RSA.</li>
  <li><strong>AES:</strong> Encrypts the actual message (symmetric, fast).</li>
  <li><strong>RSA:</strong> Encrypts the AES key (asymmetric, secure).</li>
  <li><strong>Dual Encryption:</strong> AES key is encrypted using both the receiver's and sender's public RSA keys.</li>
</ul>

<h3>🔑 Key Management</h3>
<ul>
  <li><strong>RSA Key Generation:</strong> 2048-bit key pairs are created and stored in Android Keystore using <code>KeyStoreHelper</code>.</li>
  <li><strong>Public Key Sharing:</strong> Base64-encoded public keys are saved in Firebase under each user's Contacts entry.</li>
</ul>

<h3>🔒 Message Encryption Process</h3>
<ol>
  <li><strong>AES Key Generation:</strong> A new 256-bit AES key is created per message.</li>
  <li><strong>AES Encryption:</strong> Message encrypted with <code>AES/GCM/NoPadding</code> and random 12-byte IV.</li>
  <li><strong>RSA Encryption:</strong> AES key is encrypted:
    <ul>
      <li>Once with the receiver’s public key.</li>
      <li>Once with the sender’s public key (for secure history storage).</li>
    </ul>
  </li>
  <li><strong>Payload:</strong> Includes encrypted message, AES key(s), and IV.</li>
</ol>

<h3>🔓 Message Decryption Process</h3>
<ol>
  <li><strong>RSA Decryption:</strong> AES key is decrypted using the device's private RSA key.</li>
  <li><strong>AES Decryption:</strong> Message decrypted with the recovered AES key and IV.</li>
</ol>

  ## UI

  <table>
  <tr>
    <td><img src="https://github.com/RakibHasan106/Barta_a_Messenger_App/blob/master/Images/Splash%20Screen.jpg" height="400px" width="200px"></td>
    <td><img src="https://github.com/RakibHasan106/Barta_a_Messenger_App/blob/master/Images/login%20page.jpg" height="400px" width="200px"</td>
      <td><img src="https://github.com/RakibHasan106/Barta_a_Messenger_App/blob/master/Images/signup%20page.png" height="400px" width="200px"</td>
        <td><img src="https://github.com/RakibHasan106/Barta_a_Messenger_App/blob/master/Images/OTP%20number.png" height="400px" width="200px"</td>
  </tr>
  <tr>
    <td><img src="https://github.com/RakibHasan106/Barta_a_Messenger_App/blob/master/Images/Verification%20Mail%20Sent.png" height="400px" width="200px"</td>
      <td><img src="https://github.com/RakibHasan106/Barta_a_Messenger_App/blob/master/Images/Chat%20List.jpg" height="400px" width="200px"</td>
        <td><img src="https://github.com/RakibHasan106/Barta_a_Messenger_App/blob/master/Images/Inbox.jpg" height="400px" width="200px"</td>
          <td><img src="https://github.com/RakibHasan106/Barta_a_Messenger_App/blob/master/Images/profile_view.jpg" height="400px" width="200px"</td>
            
  </tr>
  <tr>
    <td><img src="https://github.com/RakibHasan106/Barta_a_Messenger_App/blob/master/Images/Friend_Request.png" height="400px" width="200px"</td>
            <td><img src="https://github.com/RakibHasan106/Barta_a_Messenger_App/blob/master/Images/Friend%20Request.jpg" height="400px" width="200px"</td>
                <td><img src="https://github.com/RakibHasan106/Barta_a_Messenger_App/blob/master/Images/Edit_Profile.png" height="400px" width="200px"</td>
                  <td><img src="https://github.com/RakibHasan106/Barta_a_Messenger_App/blob/master/Images/Settings.jpg" height="400px" width="200px"</td>
  </tr>
</table>

<h2>Contributors</h2>
<ul>
  <li><a href="https://github.com/Abtahe103" target="_blank"><strong>Mohammad Abtahe Alam</strong></a></li>
  <li><a href="https://github.com/RakibHasan106" target="_blank"><strong>Md. Rakibul Hasan Adnan</strong></a></li>
</ul>
  




