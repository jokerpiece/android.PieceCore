package jp.co.jokerpiece.piecebase;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import jp.co.jokerpiece.piecebase.config.Config;

/**
 * Created by kaku on 2015/05/28.
 */
public class StampAdapter extends BaseAdapter {


        private static class ViewHolder {
            public ImageView imageView;
            public ImageView BackGroundImageView;
            public ImageView RedBarRtImageView;
            public ImageView RedBarLtImageView;
            public TextView  textView;
        }

        private LayoutInflater inflater;
        private int layoutId;
        Bitmap bmp;
        Bitmap BackGroundbp;
        Bitmap RedBar;
        SharedPreferences pref;

        public StampAdapter(Context context,int layoutId) {
            super();
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.layoutId = layoutId;
            this.bmp = BitmapFactory.decodeResource(context.getResources(),R.drawable.stamp);
            BackGroundbp = BitmapFactory.decodeResource(context.getResources(),R.drawable.stampbase);
            RedBar = BitmapFactory.decodeResource(context.getResources(),R.drawable.stampbar);
            pref= context.getSharedPreferences("setting", Activity.MODE_PRIVATE | Activity.MODE_MULTI_PROCESS);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null) {
                // main.xml の <GridView .../> に grid_items.xml を inflate して convertView とする
                convertView = inflater.inflate(layoutId, parent, false);
                // ViewHolder を生成
                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.stamp_image);
                holder.BackGroundImageView = (ImageView) convertView.findViewById(R.id.BackGround);
                holder.textView = (TextView) convertView.findViewById(R.id.textview);
                holder.RedBarRtImageView = (ImageView) convertView.findViewById(R.id.RedBarRt);
                holder.RedBarLtImageView = (ImageView) convertView.findViewById(R.id.RedBarLt);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.BackGroundImageView.setImageBitmap(BackGroundbp);
            String s = new Integer(position+1).toString();
            holder.textView.setText(s);

            if(StampFragment.get_point == position + 1 ) {
                AnimationSet animationSet = new AnimationSet(true);
                ScaleAnimation scaleAnimation = new ScaleAnimation(2, 1, 2, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animationSet.addAnimation(scaleAnimation);
                scaleAnimation.setDuration(500);
                holder.imageView.setImageBitmap(bmp);
                if(StampFragment.currentPoint < StampFragment.get_point) {
                    holder.imageView.startAnimation(animationSet);
                    StampFragment.currentPoint = StampFragment.get_point;
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("setting", StampFragment.currentPoint);
                    editor.commit();
                }
            }
            if(StampFragment.get_point > position + 1 ) {
                holder.imageView.setImageBitmap(bmp);
            }
            if((position+1) % 5 != 0 && (position+1) % 5 != 1 && position +1 != StampFragment.total_point){
                holder.RedBarLtImageView.setImageBitmap(RedBar);
                holder.RedBarRtImageView.setImageBitmap(RedBar);
            }
            if((position+1) % 5 == 1){
                holder.RedBarRtImageView.setImageBitmap(RedBar);
            }
            if((position+1) % 5 == 0 ){
                holder.RedBarLtImageView.setImageBitmap(RedBar);
            }
            return convertView;
        }

        @Override
        public int getCount() {

            return StampFragment.total_point;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

}
