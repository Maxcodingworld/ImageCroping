package com.justbooksclc.sanyam.imageintents;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements ImagePickAndCropFragment.OnImageCropFragment{
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.croppedImageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterPopup(v);
            }
        });
    }

    private void showFilterPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.popup_gallery_camera, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.gallery:
                        FrameLayout fragment_container_gallery = (FrameLayout) findViewById(R.id.frame_container);
                        ImagePickAndCropFragment frag = new ImagePickAndCropFragment();
                        frag.setIntentType(0);
                        frag.setratio(270, 130);
                        frag.setCenterCrop(0);
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame_container,frag)
                                .addToBackStack("frag_gallery")
                                .commit();
                        fragment_container_gallery.setVisibility(View.VISIBLE);

                        return true;

                    case R.id.camera:
                        FrameLayout fragment_container_camera = (FrameLayout) findViewById(R.id.frame_container);
                        ImagePickAndCropFragment frag2 = new ImagePickAndCropFragment();
                        frag2.setIntentType(1);
                        frag2.setratio(170, 130);
                        frag2.setCenterCrop(0);
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame_container,frag2)
                                .addToBackStack("frag_camera")
                                .commit();
                        fragment_container_camera.setVisibility(View.VISIBLE);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    @Override
    public void onCropClick(Bitmap croppedImage,String imageString) {
        imageView.setImageBitmap(croppedImage);
        FrameLayout fragment_container = (FrameLayout) findViewById(R.id.frame_container);
        fragment_container.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onNothingSelected() {
        FrameLayout fragment_container = (FrameLayout) findViewById(R.id.frame_container);
        fragment_container.setVisibility(View.INVISIBLE);
        Toast.makeText(this,"You haven't picked anything",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onfileisgreater2MB() {
        FrameLayout fragment_container = (FrameLayout) findViewById(R.id.frame_container);
        fragment_container.setVisibility(View.INVISIBLE);
        Toast.makeText(this,"Image should not be greater than 2MB",Toast.LENGTH_SHORT).show();
    }
}



//        cropButton = (Button)findViewById(R.id.crop_button);
//        cropButton.setVisibility(View.GONE);
//        croppedImageView = (ImageView)findViewById(R.id.croppedImageView);
//        cropImageView = (CropImageView)findViewById(R.id.cropImageView);
//
//        croppedImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                croppedImageView.setVisibility(View.GONE);
//                Intent intent = new Intent(Intent.ACTION_PICK,
//                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent, 0);
//            }
//        });
//
//
//        cropButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Get cropped image, and show result.
//                croppedImageView.setVisibility(View.VISIBLE);
//                croppedImageView.setImageBitmap(cropImageView.getCroppedBitmap());
//                cropImageView.setVisibility(View.GONE);
//                cropButton.setVisibility(View.GONE);
//            }
//        });



//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
////        if(resultCode == RESULT_OK && requestCode == 0){
////            Uri selectedImage = data.getData();
////            String[] filePathColumn = {MediaStore.Images.Media.DATA};
////            Cursor cursor = getContentResolver().query(selectedImage,
////                    filePathColumn, null, null, null);
////            if (cursor == null) {
////                imgPath = selectedImage.getPath();
////            } else {
////                cursor.moveToFirst();
////                int idx = cursor.getColumnIndex(filePathColumn[0]);
////                imgPath = cursor.getString(idx);
////                cursor.close();
////            }
////            if(imgPath == null)
////                return;
////            Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
////            cropImageView.setCustomRatio(16,9);
////            cropImageView.setImageBitmap(bitmap);
////            cropButton.setVisibility(View.VISIBLE);
////        }
//    }
