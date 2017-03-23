package jp.co.jokerpiece.piecebase.InterFaces;

/**
 * Created by Antifuture on 2017/03/22.
 */

//このクラスは、プッシュ通知が届いた時に、プッシュ通知Infoを押した後の画面遷移の処理をする場所です。
public interface PushNotifyTransition
{
    //バックグラウンドからアプリを起動した場合の処理、このメソッドはホームページにしたクラスで書くべき。
    void pushNotifyTransitionInBackGround();

    //foregroundからプッシュ通知Infoを押した後の処理、このメソッドはMainBaseActivity、またはMainBaseActivityを継続したクラスで書くべき。
    void pushNotifyTransitionInForeground();
}
