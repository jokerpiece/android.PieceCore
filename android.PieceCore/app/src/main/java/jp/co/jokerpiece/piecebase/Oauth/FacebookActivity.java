package jp.co.jokerpiece.piecebase.Oauth;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.util.ArrayList;
import java.util.Arrays;

import jp.co.jokerpiece.piecebase.R;
import jp.co.jokerpiece.piecebase.SnsFragment;

/**
 * Created by kaku on 2015/06/01.
 */

public class FacebookActivity extends FragmentActivity {


    private static final String PERMISSION = "publish_actions";
//    private static final Location SEATTLE_LOCATION = new Location("") {
//        {
//            setLatitude(47.6097);
//            setLongitude(-122.3331);
//        }
//    };

    private final String PENDING_ACTION_BUNDLE_KEY =
            "com.facebook.samples.hellofacebook:PendingAction";

    private Button postStatusUpdateButton;
    private Button postPhotoButton;
    private ProfilePictureView profilePictureView;
    private TextView greeting;
    private PendingAction pendingAction = PendingAction.NONE;
    private boolean canPresentShareDialog;
    private boolean canPresentShareDialogWithPhotos;
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private ShareDialog shareDialog;
    private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onCancel() {
            //AppUtil.debugLog("HelloFacebook", "Canceled");
        }

        @Override
        public void onError(FacebookException error) {
         //   AppUtil.debugLog("HelloFacebook", String.format("Error: %s", error.toString()));
//            String title = getString(R.string.error);
//            String alertMessage = error.getMessage();
//            showResult(title, alertMessage);
        }

        @Override
        public void onSuccess(Sharer.Result result) {
           // AppUtil.debugLog("HelloFacebook", "Success!");
//            if (result.getPostId() != null) {
//                String title = getString(R.string.success);
//                String id = result.getPostId();
//                String alertMessage = getString(R.string.successfully_posted_post, id);
//                showResult(title, alertMessage);
//            }
        }

        private void showResult(String title, String alertMessage) {
            new AlertDialog.Builder(FacebookActivity.this)
                    .setTitle(title)
                    .setMessage(alertMessage)
                    .setPositiveButton(R.string.ok, null)
                    .show();
        }
    };

    private enum PendingAction {
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.facebookview);
        callbackManager = CallbackManager.Factory.create();
        //postStatusUpdateButton = (Button) findViewById(R.id.postStatusUpdateButton);
        postPhotoButton = (Button) findViewById(R.id.postPhotoButton);
        greeting = (TextView)findViewById(R.id.tv_message);
        profilePictureView = (ProfilePictureView) findViewById(R.id.profilePicture);
        // postStatusUpdateButton.setVisibility(View.GONE);
        postPhotoButton.setVisibility(View.GONE);

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handlePendingAction();
                        updateUI();
//                        postPhotoButton.setVisibility(View.VISIBLE);
//                        postStatusUpdateButton.setVisibility(View.VISIBLE);
                        //onClickPostStatusUpdate();

                    }

                    @Override
                    public void onCancel() {
                        if (pendingAction != PendingAction.NONE) {
//                            showAlert();
                            pendingAction = PendingAction.NONE;
                        }
                        updateUI();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        if (pendingAction != PendingAction.NONE
                                && exception instanceof FacebookAuthorizationException) {
//                            showAlert();
                            pendingAction = PendingAction.NONE;
                        }
                        updateUI();
                    }

//                    private void showAlert() {
//                        new AlertDialog.Builder(FacebookActivity.this)
//                                .setTitle(R.string.cancelled)
//                                .setMessage(R.string.permission_not_granted)
//                                .setPositiveButton(R.string.ok, null)
//                                .show();
//                    }
                });

        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(
                callbackManager,
                shareCallback);

        if (savedInstanceState != null) {
            String name = savedInstanceState.getString(PENDING_ACTION_BUNDLE_KEY);
            pendingAction = PendingAction.valueOf(name);
        }



        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                updateUI();
                // It's possible that we were waiting for Profile to be populated in order to
                // post a status update.
                handlePendingAction();
            }
        };

//        postStatusUpdateButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                onClickPostStatusUpdate();
//            }
//        });


        postPhotoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onClickPostPhoto();
            }
        });

        // Can we present the share dialog for regular links?
        canPresentShareDialog = ShareDialog.canShow(
                ShareLinkContent.class);

        // Can we present the share dialog for photos?
        canPresentShareDialogWithPhotos = ShareDialog.canShow(
                SharePhotoContent.class);
    }
    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
        updateUI();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
    }
    private void updateUI() {
        boolean enableButtons = AccessToken.getCurrentAccessToken() != null;

       // postStatusUpdateButton.setEnabled(enableButtons || canPresentShareDialog);
        postPhotoButton.setEnabled(enableButtons || canPresentShareDialogWithPhotos);

        Profile profile = Profile.getCurrentProfile();
        if (enableButtons && profile != null) {
            profilePictureView.setProfileId(profile.getId());
            postPhotoButton.setVisibility(View.VISIBLE);
           // postStatusUpdateButton.setVisibility(View.VISIBLE);
            String name = profile.getName();
            greeting.setText(getString(R.string.hello_user ) + name);
        } else {
            profilePictureView.setProfileId(null);
            //LoginManager.getInstance().logOut();
            greeting.setText(getString(R.string.Facebook_msg));
           // postStatusUpdateButton.setVisibility(View.GONE);
            postPhotoButton.setVisibility(View.GONE);
        }
    }
    private void onClickPostPhoto() {
        performPublish(PendingAction.POST_PHOTO, canPresentShareDialogWithPhotos);
    }
    private void handlePendingAction() {
        PendingAction previouslyPendingAction = pendingAction;
        // These actions may re-set pendingAction if they are still pending, but we assume they
        // will succeed.
        pendingAction = PendingAction.NONE;

        switch (previouslyPendingAction) {
            case NONE:
                break;
            case POST_PHOTO:
                postPhoto();
                break;
            case POST_STATUS_UPDATE:
                postStatusUpdate();
                break;
        }
    }

    private void onClickPostStatusUpdate() {
        performPublish(PendingAction.POST_STATUS_UPDATE, canPresentShareDialog);
    }
    private void postStatusUpdate() {
        Profile profile = Profile.getCurrentProfile();
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle(this.getString(R.string.Facebook_title))
                .setContentDescription(
                       this.getString(R.string.Facebook_msg))
                .setContentUrl(Uri.parse("http://developers.facebook.com/docs/android"))
                .build();
        if (canPresentShareDialog) {
            shareDialog.show(linkContent);
        } else if (profile != null && hasPublishPermission()) {
            ShareApi.share(linkContent, shareCallback);
        } else {
            pendingAction = PendingAction.POST_STATUS_UPDATE;
        }
    }
    private void postPhoto() {
        Bitmap image = BitmapFactory.decodeFile(SnsFragment.filePath);
        SharePhoto sharePhoto = new SharePhoto.Builder().setBitmap(image).build();
        ArrayList<SharePhoto> photos = new ArrayList<>();
        photos.add(sharePhoto);

        SharePhotoContent sharePhotoContent =
                new SharePhotoContent.Builder().setPhotos(photos).build();
        if (canPresentShareDialogWithPhotos) {
            shareDialog.show(sharePhotoContent);
        } else if (hasPublishPermission()) {
            ShareApi.share(sharePhotoContent, shareCallback);
        } else {
            pendingAction = PendingAction.POST_PHOTO;
        }
    }
    private boolean hasPublishPermission() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && accessToken.getPermissions().contains("publish_actions");
    }
    private void performPublish(PendingAction action, boolean allowNoToken) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            pendingAction = action;
            if (hasPublishPermission()) {
                // We can do the action right away.
                handlePendingAction();
                return;
            } else {
                // We need to get new permissions, then complete the action when we get called back.
                LoginManager.getInstance().logInWithPublishPermissions(
                        this,
                        Arrays.asList(PERMISSION));
                return;
            }
        }

        if (allowNoToken) {
            pendingAction = action;
            handlePendingAction();
        }
    }


}