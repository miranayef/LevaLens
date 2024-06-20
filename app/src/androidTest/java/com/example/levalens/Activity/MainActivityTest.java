//package com.example.levalens.Activity;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.widget.ImageButton;
//
//import androidx.camera.view.PreviewView;
//import androidx.drawerlayout.widget.DrawerLayout;
//
//import com.example.levalens.Helper.CameraHelper;
//import com.example.levalens.Helper.ImagePickerHelper;
//import com.example.levalens.Helper.NavigationHelper;
//import com.google.android.material.bottomsheet.BottomSheetDialog;
//
//import junit.framework.TestCase;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//
//@RunWith(MockitoJUnitRunner.class)
//public class MainActivityTest {
//
//    @Mock
//    private CameraHelper cameraHelper;
//
//    @Mock
//    private ImagePickerHelper imagePickerHelper;
//
//    @Mock
//    private NavigationHelper navigationHelper;
//
//    @Mock
//    private PreviewView previewView;
//
//    @Mock
//    private DrawerLayout drawerLayout;
//
//    @Mock
//    private ImageButton flashButton;
//
//    @Mock
//    private BottomSheetDialog bottomSheetDialog;
//
//    @Mock
//    private Context context;
//
//    @Mock
//    private SharedPreferences sharedPreferences;
//
//    @Mock
//    private SharedPreferences.Editor editor;
//
//    @InjectMocks
//    private MainActivity mainActivity;
//
//    @Before
//    public void setUp() {
//        MockitoAnnotations.initMocks(this);
//        mainActivity = Mockito.spy(new MainActivity());
//        doReturn(context).when(mainActivity).getApplicationContext();
//        doReturn(sharedPreferences).when(mainActivity).getSharedPreferences(anyString(), anyInt());
//        doReturn(editor).when(sharedPreferences).edit();
//    }
//
//    @Test
//    public void testInitializeViews() {
//        // Mock findViewById calls
//        doReturn(previewView).when(mainActivity).findViewById(R.id.previewView);
//        ImageButton captureButton = mock(ImageButton.class);
//        doReturn(captureButton).when(mainActivity).findViewById(R.id.captureButton);
//        ImageButton uploadButton = mock(ImageButton.class);
//        doReturn(uploadButton).when(mainActivity).findViewById(R.id.uploadButton);
//        doReturn(flashButton).when(mainActivity).findViewById(R.id.flashButton);
//        ImageButton helpButton = mock(ImageButton.class);
//        doReturn(helpButton).when(mainActivity).findViewById(R.id.helpButton);
//        ImageButton drawerButton = mock(ImageButton.class);
//        doReturn(drawerButton).when(mainActivity).findViewById(R.id.drawerButton);
//        doReturn(drawerLayout).when(mainActivity).findViewById(R.id.drawer_layout);
//        NavigationView navigationView = mock(NavigationView.class);
//        doReturn(navigationView).when(mainActivity).findViewById(R.id.navigation_view);
//
//        mainActivity.initializeViews();
//
//        verify(captureButton).setOnClickListener(any(View.OnClickListener.class));
//        verify(uploadButton).setOnClickListener(any(View.OnClickListener.class));
//        verify(flashButton).setOnClickListener(any(View.OnClickListener.class));
//        verify(helpButton).setOnClickListener(any(View.OnClickListener.class));
//        verify(drawerButton).setOnClickListener(any(View.OnClickListener.class));
//        assertNotNull(mainActivity.navigationHelper);
//    }
//
//    // Other tests will go here
//}
