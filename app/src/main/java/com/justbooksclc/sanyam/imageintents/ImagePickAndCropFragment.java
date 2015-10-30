package com.justbooksclc.sanyam.imageintents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.isseiaoki.simplecropview.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class ImagePickAndCropFragment extends Fragment {
    private View view;
    private CropImageView cropImageView;
    private Bitmap original,croppedImage;
    private Context context;
    private int flag = 0 ;
    private int ratioX = 100 , ratioY = 100;
    private OnImageCropFragment listener;
    private String currentTimeStamp;
    private Uri cameraUri;
    private int cropCircle =0;


    public static int GALLERY_FLAG = 0, CAMERA_FLAG = 1;
    public static int BUSINESS_COLOR = R.color.business_blue,CONSUMER_COLOR = R.color.consumer_orange;
    public int color = R.color.business_blue;
    public void setIntentType(int type){
        if(type==0||type==1)
            flag = type;
    }

    public void setratio(int x,int y){
        ratioX = x;
        ratioY = y;
    }

    public void setCenterCrop(int flag){
        this.cropCircle = flag;
    }

    public void setHandlerColor(int color){
        this.color = color;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentTimeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Timestamp(System.currentTimeMillis()));
        view = inflater.inflate(R.layout.image_pick_and_crop_fragment, container, false);
        view.setVisibility(View.INVISIBLE);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final View fragView = getView();
        cropImageView = (CropImageView) fragView.findViewById(R.id.cropImageView);
        cropImageView.setHandleShowMode(CropImageView.ShowMode.SHOW_ON_TOUCH);
        cropImageView.setHandleColor(context.getResources().getColor(this.color));
        TextView cropButton = (TextView) fragView.findViewById(R.id.crop_button);
        ImageView rotaterightButton = (ImageView) fragView.findViewById(R.id.rotaterightbutton);
        ImageView rotateleftButton = (ImageView) fragView.findViewById(R.id.rotateleftbutton);
        cropImageView.setImageResource(R.drawable.image);

        cropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                croppedImage = cropImageView.getCroppedBitmap();
                String imageString = BitMapToString(croppedImage);
                listener.onCropClick(croppedImage, imageString);
                fragView.setVisibility(View.INVISIBLE);
            }
        });


        rotaterightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
            }
        });

        rotateleftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_270D);
            }
        });

        Intent intent;
        if(flag==0) {
            intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, GALLERY_FLAG);
        }else{
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    , "fm_" + currentTimeStamp);
            cameraUri = Uri.fromFile(imageFile);
            System.out.println("cameraURI " + cameraUri.toString());
            intent.putExtra(MediaStore.EXTRA_OUTPUT,cameraUri);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            startActivityForResult(intent, CAMERA_FLAG);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        view.setVisibility(View.VISIBLE);
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == GALLERY_FLAG){
                Uri imageuri = data.getData();
                float imagesize = calculateFileSize(getRealPathFromURI(context, imageuri));
                if(imagesize<=2) {
                    InputStream inputStream;
                    try {
                        inputStream = context.getContentResolver().openInputStream(imageuri);
                        original = BitmapFactory.decodeStream(inputStream);
                        cropImageView.setImageBitmap(original);
                        if (cropCircle == 1)
                            cropImageView.setCropMode(CropImageView.CropMode.CIRCLE);
                        else
                            cropImageView.setCustomRatio(ratioX, ratioY);
                    } catch (FileNotFoundException e) {
                        System.out.println("Error :" + e);
                    }
                }else
                    listener.onfileisgreater2MB();
            }
            else if(requestCode == CAMERA_FLAG){
                if(cameraUri!=null){
                    float imagesize = calculateFileSize(cameraUri.getPath());

                    if(imagesize<=2) {
                        InputStream inputStream;
                        try {
                            inputStream = context.getContentResolver().openInputStream(cameraUri);
                            original = BitmapFactory.decodeStream(inputStream);
                            cropImageView.setImageBitmap(original);
                            if (cropCircle == 1)
                                cropImageView.setCropMode(CropImageView.CropMode.CIRCLE);
                            else
                                cropImageView.setCustomRatio(ratioX, ratioY);
                        } catch (FileNotFoundException e) {
                            System.out.println("Error :" + e);
                        }
                    }else
                        listener.onfileisgreater2MB();
                }
            }
        }else{
            listener.onNothingSelected();
            view.setVisibility(View.INVISIBLE);
        }

    }

    public interface OnImageCropFragment{
        void onCropClick(Bitmap croppedImage,String imageString);
        void onNothingSelected();
        void onfileisgreater2MB();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        listener = (OnImageCropFragment) activity;
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream ByteStream=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, ByteStream);
        byte [] b=ByteStream.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public float calculateFileSize(String path)
    {
        File file = new File(path);
        if(!file.exists())
            System.out.println("file doesn't exist");
        float fileSizeInBytes = file.length();
        float fileSizeInKB = fileSizeInBytes / 1024;
        float fileSizeInMB = fileSizeInKB / 1024;
        return fileSizeInMB;
    }


    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}
