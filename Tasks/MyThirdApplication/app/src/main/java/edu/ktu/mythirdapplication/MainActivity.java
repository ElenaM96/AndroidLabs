package edu.ktu.mythirdapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements SensorEventListener, LocationListener {

    public static final String AUTO = "Automatic";
    private static final String TAG = "AndroidCameraApi";
    private SensorManager sensorManager;
    private Sensor senAccelerometer, senCompass;
    private TextView xValue, yValue, zValue, coordinates, direction;
    private boolean informationObtained;
    private Button startAndStop, takePictureButton;
    private TextureView textureView;
    private LocationManager locationManager;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private String cameraId;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSession;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private Context mContext = this;
    float orientation_Y = 0;
    float compass_or = 0;
    boolean checked = false;


    private float prev_x = 0;
    private float prev_y = 0;
    private float prev_z = 0;

    private ImageView image;
    private TextView compass;
    private float currentDegree = 0f;
    private Camera mCamera;
    private boolean isNorth = false;
    private Camera.Parameters parameters;
    private CameraManager manager;
    private GPSTracker gps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        informationObtained = false;

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        senAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senCompass = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);


        xValue = (TextView) findViewById(R.id.x_value);
        yValue = (TextView) findViewById(R.id.y_value);
        zValue = (TextView) findViewById(R.id.z_value);
        startAndStop = (Button) findViewById(R.id.start_and_stop);

        image = (ImageView) findViewById(R.id.imgCompass);
        compass = (TextView) findViewById(R.id.compass);

        coordinates = (TextView) findViewById(R.id.coordinates);
        direction = (TextView) findViewById(R.id.direction);

        startAndStop.setOnClickListener(StartAndStopButtonListener);


        textureView = (TextureView) findViewById(R.id.textureView);
        assert textureView != null;
        textureView.setSurfaceTextureListener(textureListener);

        takePictureButton = (Button) findViewById(R.id.take_photo);
        assert takePictureButton != null;

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        gpsLocation();
    }


    View.OnClickListener StartAndStopButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (senAccelerometer == null) {
                Toast.makeText(MainActivity.this, getString(R.string.no_sensor), Toast.LENGTH_LONG).show();
                return;
            }

            if (informationObtained) {
                reset();
            } else {
                set();
            }

        }
    };


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        String dir_x;
        String dir_y;
        String dir_z;



        if(mySensor.getType() == Sensor.TYPE_ACCELEROMETER){
            if(sensorEvent.values[0] > prev_x +0.300 || sensorEvent.values[0] < prev_x - 0.300){
                xValue.setText(String.valueOf(sensorEvent.values[0]));
                prev_x = sensorEvent.values[0];
            }

            if(sensorEvent.values[1] > prev_y +0.300 || sensorEvent.values[1] < prev_y - 0.300){
                yValue.setText(String.valueOf(sensorEvent.values[1]));
                prev_y = sensorEvent.values[1];
            }

            if(sensorEvent.values[2] > prev_z +0.300 || sensorEvent.values[2] < prev_z - 0.300){
                zValue.setText(String.valueOf(sensorEvent.values[2]));
                prev_z = sensorEvent.values[2];
            }

            if (sensorEvent.values[0] > 0){
                dir_x = "left";
            } else if (sensorEvent.values[0] == 0.0){
                dir_x = "center";
            } else {
                dir_x = "right";
            }

            if (sensorEvent.values[1] > 0){
                dir_y = "up";
            } else if (sensorEvent.values[1] == 0.0) {
                dir_y = "center";
            } else {
                dir_y = "down";
            }

            if (sensorEvent.values[2]> 0){
                dir_z = "forward";
            } else if (sensorEvent.values[2] == 0.0) {
                dir_z = "center";
            } else {
                dir_z = "back";
            }

            orientation_Y = sensorEvent.values[1];
            direction.setText(dir_x + " " + dir_y + " " + dir_z);

            if (!checked) {
                settingPermission();
            }

            if (sensorEvent.values[1] < 1){
                Settings.System.putInt(
                        this.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS,
                        0
                );

            } else if(sensorEvent.values[1] > 8){
                Settings.System.putInt(
                        this.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS,
                        255
                );
            }

        } else if(mySensor.getType() == Sensor.TYPE_ORIENTATION){

            // get the angle around the z-axis rotated
            float degree = Math.round(sensorEvent.values[0]);

            compass.setText("Heading: " + Float.toString(sensorEvent.values[0]));
            compass_or = sensorEvent.values[0];

            // create a rotation animation (reverse turn degree degrees)
            RotateAnimation ra = new RotateAnimation(
                    currentDegree,
                    -degree,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            // how long the animation will take place
            ra.setDuration(210);

            // set the animation after the end of the reservation status
            ra.setFillAfter(true);

            // Start the animation
            image.startAnimation(ra);
            currentDegree = -degree;

            if (sensorEvent.values[0] < 0.2 ){
                isNorth = true;
                reset();
                takePicture();

            }

        }

        if(compass_or > 179.5 && compass_or < 180.2 ){
            if( orientation_Y > 9.4 && orientation_Y < 9.8){
                closeCamera();
                sosLight();
                openCamera();
            }

        }
    }

    public void settingPermission() {
        checked = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 200);

            }
        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null){
            coordinates.setText(getString(R.string.latitude_text)+" "
                    +location.getLatitude()+" "
                    +getString(R.string.longitude_text)+" "
                    +location.getLongitude());
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };


    private void set(){
        sensorManager.registerListener(MainActivity.this,senAccelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(MainActivity.this,senCompass,SensorManager.SENSOR_DELAY_GAME);
        startAndStop.setText(getString(R.string.stop));
        informationObtained = true;

    }

    private  void reset(){
        startAndStop.setText(getString(R.string.start));
        sensorManager.unregisterListener(MainActivity.this,senAccelerometer);
        sensorManager.unregisterListener(MainActivity.this,senCompass);
        informationObtained = false;

    }

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.e(TAG,"onOpened");
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {

        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    final CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            Toast.makeText(MainActivity.this,"Saved:"+file,Toast.LENGTH_SHORT).show();
            createCameraPreview();
        }
    };



    protected void startBackgroundThread(){
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    protected void stopBackgroundThread(){
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    protected void takePicture(){
        if (null == cameraDevice) {
            Log.e(TAG,"camera device is null");
            return;
        }

        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            if (characteristics != null){
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length){
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            final ImageReader reader = ImageReader.newInstance(width,height,ImageFormat.JPEG,1);
            List<Surface> outputSurfaces = new ArrayList<>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION,ORIENTATIONS.get(rotation));
            final File file = new File(Environment.getExternalStorageDirectory()+"/pic.jpg");

            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader imageReader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }catch (IOException e){
                        e.printStackTrace();
                    } finally {
                        if(image != null){
                            image.close();
                        }
                    }
                }

                private  void save (byte[] bytes) throws IOException {
                    OutputStream output = null;
                    try {
                        output = new FileOutputStream(file);
                        output.write(bytes);
                    } finally {
                        if(null != output){
                            output.close();
                            if(isNorth){
                                isNorth = false;
                                changeActivity();
                            }
                        }
                    }
                }
            };

            reader.setOnImageAvailableListener(readerListener,mBackgroundHandler);

            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    Toast.makeText(MainActivity.this,"Saved:"+ file,Toast.LENGTH_SHORT).show();
                    createCameraPreview();
                }
            };

            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    try{
                        session.capture(captureBuilder.build(),captureListener,mBackgroundHandler);
                    } catch (CameraAccessException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                }
            },mBackgroundHandler);

        } catch (CameraAccessException e){
            e.printStackTrace();
        }

    }

    protected  void createCameraPreview(){
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert  texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(),imageDimension.getHeight());
            Surface surface = new Surface(texture);

            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCapture) {
                    if (null == cameraDevice){
                        return;
                    }
                    cameraCaptureSession = cameraCapture;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(MainActivity.this,"Configuration change",Toast.LENGTH_SHORT).show();
                }
            },null);
        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }

    private void openCamera(){
        manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG,"is camera open");

        try {
            cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert  map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];

            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId,stateCallback,null);
        } catch (CameraAccessException e){
            e.printStackTrace();
        }
        Log.e(TAG,"openCamera X");
    }

    protected void updatePreview(){
        if(null == cameraDevice){
            Log.e(TAG,"updatePreview error, return");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE,CameraMetadata.CONTROL_MODE_AUTO);
        try{
            cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(),null,mBackgroundHandler);
        } catch (CameraAccessException e){
            e.printStackTrace();
        }
    }

    private  void closeCamera(){
        if(null != cameraDevice){
            cameraDevice.close();
            cameraDevice =  null;
        }
        if(null != imageReader){
            imageReader.close();
            imageReader = null;
        }
    }

    private void changeActivity(){
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode==REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(MainActivity.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }//---------------------------------------------------------------------------------------------------------------------------GPS
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the

                    // contacts-related task you need to do.

                    gps = new GPSTracker(mContext, MainActivity.this);

                    // Check if GPS enabled
                    if (gps.canGetLocation()) {

                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();

                        // \n is for new line
                        coordinates.setText("Your Location is - \nLat: " + latitude + "\nLong: ");
                        //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                    } else {
                        // Can't get location.
                        // GPS or network is not enabled.
                        // Ask user to enable GPS/network in settings.
                        gps.showSettingsAlert();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    Toast.makeText(mContext, "You need to grant permission", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
        //---------------------------------------------------------------------------------------------------------------------------GPS END
    }

    private void turnFlashlightOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                manager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
                String cameraId = null; // Usually front camera is at 0 position.
                if (manager != null) {
                    cameraId = manager.getCameraIdList()[0];
                    manager.setTorchMode(cameraId, true);
                }
            } catch (CameraAccessException e) {
                Log.e(TAG, e.toString());
            }
        } else {
            mCamera = Camera.open();
            parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(parameters);
            mCamera.startPreview();
        }
    }

    private void turnFlashlightOff() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                String cameraId;
                manager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
                if (manager != null) {
                    cameraId = manager.getCameraIdList()[0]; // Usually front camera is at 0 position.
                    manager.setTorchMode(cameraId, false);
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            mCamera = Camera.open();
            parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(parameters);
            mCamera.stopPreview();
        }
    }

    public void sosLight(){
        String sos = "111000111";
        for (int x = 1; x < sos.length(); x++){
            if(sos.charAt(x)=='1'){
                try {
                    turnFlashlightOn();
                    sleep(100);
                    turnFlashlightOff();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            } else {
                try {
                    turnFlashlightOn();
                    sleep(500);
                    turnFlashlightOff();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void gpsLocation(){
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {
            Toast.makeText(mContext,"You need have granted permission",Toast.LENGTH_SHORT).show();
            gps = new GPSTracker(mContext, MainActivity.this);

            // Check if GPS enabled
            if (gps.canGetLocation()) {

                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

                // \n is for new line
                coordinates.setText("Your Location is - \nLat: " + latitude + "\nLong: " + longitude);
                // Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            } else {
                // Can't get location.
                // GPS or network is not enabled.
                // Ask user to enable GPS/network in settings.
                gps.showSettingsAlert();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (senAccelerometer != null){
            sensorManager.unregisterListener(MainActivity.this,senAccelerometer);
            sensorManager.unregisterListener(MainActivity.this,senCompass);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        stopBackgroundThread();

        this.locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (senAccelerometer != null && informationObtained){
            sensorManager.registerListener(MainActivity.this, senAccelerometer,SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(MainActivity.this, senCompass,SensorManager.SENSOR_DELAY_GAME);

        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        startBackgroundThread();
        if(textureView.isAvailable()){
            openCamera();
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }

        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,400,1,this);
    }
}

