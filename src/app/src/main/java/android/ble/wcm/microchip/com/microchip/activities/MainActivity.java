package android.ble.wcm.microchip.com.microchip.activities;

import android.ble.wcm.microchip.com.microchip.DataStore;
import android.ble.wcm.microchip.com.microchip.R;
import android.ble.wcm.microchip.com.microchip.adapters.BleListAdapter;
import android.ble.wcm.microchip.com.microchip.adapters.MenuListAdapter;
import android.ble.wcm.microchip.com.microchip.api.Api;
import android.ble.wcm.microchip.com.microchip.api.ApiService;
import android.ble.wcm.microchip.com.microchip.api.model.Status.Status;
import android.ble.wcm.microchip.com.microchip.ble.BleInterface;
import android.ble.wcm.microchip.com.microchip.ble.BleService;
import android.ble.wcm.microchip.com.microchip.fragments.MainFragment;
import android.ble.wcm.microchip.com.microchip.model.ResponseModel;
import android.ble.wcm.microchip.com.microchip.provider.response.ResponseColumns;
import android.ble.wcm.microchip.com.microchip.provider.response.ResponseCursor;
import android.ble.wcm.microchip.com.microchip.provider.response.ResponseProvider;
import android.ble.wcm.microchip.com.microchip.utils.GGson;
import android.ble.wcm.microchip.com.microchip.view.SettingsBleConnectionHeader;
import android.ble.wcm.microchip.com.microchip.view.SettingsFooterAttributionView;
import android.ble.wcm.microchip.com.microchip.view.SettingsHeaderView;
import android.ble.wcm.microchip.com.microchip.widget.MicrochipDrawerLayout;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by: WillowTree
 * Date: 11/18/14
 * Time: 3:39 PM.
 */
public class MainActivity extends BaseActivity implements MainFragment.MainListener, LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener, BleInterface {

    private final static int RESPONSE_CALLBACK_ID = 1234;
    private static final int FETCH_DATA = 2;
    private static final int ADD_DEVICE = 3;
    private static final int CLEAR_DEVICE_ADAPTER = 4;
    private static final int CHECK_BLUETOOTH = 5;

    private static final String DEVICE = "device";

    private static final int FETCH_DATA_DELAY = 1000;
    private static final int CLEAR_DEVICES_DELAY = 10000;
    private static final int CHECK_BLUETOOTH_DELAY = 1000;

    private static final int REQUEST_ENABLE_BT = 4321;

    private Toolbar toolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private MicrochipDrawerLayout mDrawerLayout;
    private RecyclerView drawerRecyclerView;
    private MenuListAdapter adapter;

    private FrameLayout contentView;
    private MainFragment mainFragment;

    private RelativeLayout settingsContainer;
    private ListView settingsListView;
    private BleListAdapter bleDevicesAdapter;
    private SettingsBleConnectionHeader bleConnectionHeader;

    public BleService bleService;
    private BleReceiver bleReceiver;
    private MicrochipBleConnection bleConnection;

    private MenuItem deleteMenuItem;
    private MenuItem settingsMenuItem;

    private int settingsWidth;
    private int settingsHeight;

    private LocalBroadcastManager broadcastManager;
    private ApiReceiver apiReceiver;
    private boolean fetching = false;

    private boolean showingBluetoothDialog = false;

    private DrawerListener drawerListener;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case FETCH_DATA:
                    // Fetch more data
                    if(!DataStore.getAwsAmi(MainActivity.this).isEmpty() && !fetching) {
                        fetching = true;
                        Api.getStatus(MainActivity.this);
                    }

                    if(mainFragment != null){
                        mainFragment.updateContent();
                    }

                    sendJobDelayedMessage(FETCH_DATA, FETCH_DATA_DELAY);
                    break;

                case ADD_DEVICE:
                    BluetoothDevice device = GGson.fromJson(message.getData().getString(DEVICE), BluetoothDevice.class);
                    bleDevicesAdapter.addDevice(device);
                    break;

                case CLEAR_DEVICE_ADAPTER:
                    // Clear Adapter
                    if(bleService != null && bleService.isInitialized() && bleService.isScanning()){
                        bleDevicesAdapter.clearDevicesNotConnected();
                    }
                    sendJobDelayedMessage(CLEAR_DEVICE_ADAPTER, CLEAR_DEVICES_DELAY);
                    break;

                case CHECK_BLUETOOTH:
                    if(bleService != null && !bleService.isBluetoothEnabled()){
                        bleService.disconnect();
                        bleService.close();
                        bleDevicesAdapter.clear();
                    }

                    sendJobDelayedMessage(CHECK_BLUETOOTH, CHECK_BLUETOOTH_DELAY);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        setupDrawerLayout();
        setupActionBar();
        setupSettingsListView();

        mainFragment = new MainFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(contentView.getId(), mainFragment);
        ft.commit();

        getSupportLoaderManager().initLoader(RESPONSE_CALLBACK_ID, null, this);

        broadcastManager = LocalBroadcastManager.getInstance(this);
        apiReceiver = new ApiReceiver();
        bleReceiver = new BleReceiver();
        bleConnection = new MicrochipBleConnection();
    }

    @Override
    public void onResume(){
        super.onResume();
        broadcastManager.registerReceiver(apiReceiver, new IntentFilter(ApiService.BROADCAST));
        broadcastManager.registerReceiver(bleReceiver, new IntentFilter(BleService.BROADCAST));
        startJob(FETCH_DATA);
        startJob(CLEAR_DEVICE_ADAPTER);
        startJob(CHECK_BLUETOOTH);

        if(settingsContainer.getVisibility() == View.VISIBLE){
            checkStartBleScanning();
        }

        // Bind to ble service
        Intent intent= new Intent(this, BleService.class);
        bindService(intent, bleConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause(){
        super.onPause();
        stopJob(FETCH_DATA);
        stopJob(CLEAR_DEVICE_ADAPTER);
        stopJob(CHECK_BLUETOOTH);

        broadcastManager.unregisterReceiver(apiReceiver);
        broadcastManager.unregisterReceiver(bleReceiver);
        stopScanning();

        if(bleService != null){
            bleService.disconnect();
            unbindService(bleConnection);
        }

        closeDrawer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_CANCELED){
            Toast.makeText(this, getString(R.string.bluetooth_not_enabled), Toast.LENGTH_SHORT).show();
        }else{
            bleService.initialize(this);
            startScanning();
        }
    }

    @Override
    public void onBackPressed(){
        if(settingsContainer.getVisibility() == View.VISIBLE){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                concealCircleAnimation(settingsContainer, settingsWidth, settingsHeight);
            } else {
                concealSquareAnimation(settingsContainer, settingsWidth, settingsHeight);
            }

        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.settings, menu);
        getMenuInflater().inflate(R.menu.delete, menu);

        settingsMenuItem = menu.findItem(R.id.menu_settings);
        deleteMenuItem = menu.findItem(R.id.menu_delete);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(GravityCompat.START);
        settingsMenuItem.setVisible(!drawerOpen);
        deleteMenuItem.setVisible(drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.isDrawerIndicatorEnabled()) {
            if (mDrawerToggle.onOptionsItemSelected(item)) {
                return true;
            }
        }
        // Handle your other action bar items...
        switch (item.getItemId()) {
            case R.id.menu_settings:
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    revealCircleAnimation(settingsContainer, settingsWidth, settingsHeight);
                } else {
                    revealSquareAnimation(settingsContainer, settingsWidth, settingsHeight);
                }
                break;

            case R.id.menu_delete:
                adapter.removeAllItems();
                ResponseProvider.deleteAll(this);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void findViews() {

        // Find Views
        contentView = (FrameLayout) findViewById(R.id.container);
        mDrawerLayout = (MicrochipDrawerLayout) findViewById(R.id.drawer_layout);
        drawerRecyclerView = (RecyclerView) findViewById(R.id.navigation_drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        settingsContainer = (RelativeLayout) findViewById(R.id.settings_container);
        settingsListView = (ListView) findViewById(R.id.settings_list_view);

        Toolbar settingsToolBar = (Toolbar) findViewById(R.id.settings_toolbar);

        // Settings Container Layout Listener
        settingsContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                settingsContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                settingsHeight = settingsContainer.getMeasuredHeight();
                settingsWidth = settingsContainer.getMeasuredWidth();
                settingsContainer.setVisibility(View.GONE);
            }
        });

        // Setting ToolBar
        settingsToolBar.setTitle(getString(R.string.settings));
        settingsToolBar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        settingsToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void setupActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.drawer_close));
    }

    private void setupDrawerLayout() {
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new MenuListAdapter(this, layoutManager);
        adapter.setItems(ResponseProvider.getAll(this));

        drawerRecyclerView.setLayoutManager(layoutManager);
        drawerRecyclerView.setItemAnimator(new DefaultItemAnimator());
        drawerRecyclerView.setAdapter(adapter);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                // Work magic
                if(mainFragment != null)
                    mainFragment.setSlideOffset(slideOffset);
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(getString(R.string.drawer_close));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()

                if(drawerListener != null)
                    drawerListener.onDrawerClosed();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(getString(R.string.drawer_open));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setDrawerViewWithoutIntercepting(drawerRecyclerView);
    }

    private void setupSettingsListView() {
        SettingsHeaderView headerView = new SettingsHeaderView(this);
        bleConnectionHeader = new SettingsBleConnectionHeader(this);
        SettingsFooterAttributionView footerAttributionView = new SettingsFooterAttributionView(this);

        settingsListView.addHeaderView(headerView, null, false);
        settingsListView.addHeaderView(bleConnectionHeader, null, false);
        settingsListView.addFooterView(footerAttributionView, null, false);

        bleDevicesAdapter = new BleListAdapter(this);
        bleDevicesAdapter.setSettingsBleConnectionHeader(bleConnectionHeader);
        settingsListView.setAdapter(bleDevicesAdapter);
        settingsListView.setOnItemClickListener(this);
    }

    public void closeDrawer(){
        closeDrawer(null);
    }

    public void closeDrawer(DrawerListener listener) {
        drawerListener = listener;
        // If drawer layout is open, close it.
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onSettingsOpened() {
        checkStartBleScanning();
        bleDevicesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSettingsClosed() {
        stopScanning();
        bleDevicesAdapter.clearDevicesNotConnected();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int index = Integer.parseInt(view.getTag().toString());

        if(bleService != null && bleService.isInitialized()){
            if(bleDevicesAdapter.selected != index){
                bleDevicesAdapter.setSelectedIndex(index);
                verifyBleServiceIsInitialized();
                bleService.connect(bleDevicesAdapter.items.get(index).getAddress());
            }else{
                bleService.disconnect();
                bleDevicesAdapter.setSelectedIndex(BleListAdapter.NO_SELECTION);
                DataStore.clearBleDevice(this);

                if(bleService.isScanning()){
                    bleConnectionHeader.setStatusScanning();
                }
            }
        }
    }

    public interface DrawerListener{
        public void onDrawerClosed();
    }

    @Override
    public void onSwitchesLabelXPosition(int x) {
        if(x != 0){
            DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) drawerRecyclerView.getLayoutParams();
            params.width = x;
            drawerRecyclerView.setLayoutParams(params);
        }
    }

    @Override
    public void onWriteLedToDevice(Status status) {
        if(bleService != null && bleService.isInitialized() && bleService.isConnected()){
            bleService.writeLeds(status);
        }
    }

    private class ApiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            int api_type = intent.getIntExtra(ApiService.API_TYPE, -1);
            int status = intent.getExtras().getInt(ApiService.STATUS);

            switch (api_type){
                case ApiService.TYPE_GET_STATUS:
                    switch (status){
                        case ApiService.SUCCESS:
                            fetching = false;
                            break;
                        case ApiService.ERROR:
                            fetching = false;
                            break;
                    }
                    break;
            }

        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, ResponseColumns.CONTENT_URI, null, null, null,
                ResponseColumns.DEFAULT_ORDER + " DESC LIMIT 1");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        ResponseCursor responseCursor = new ResponseCursor(cursor);
        ResponseModel item = ResponseProvider.getSingleItem(responseCursor);

        if(item != null && adapter.items != null){
            if(adapter.items.size() == 0 || !adapter.items.get(0).sha1.equals(item.sha1)){
                adapter.addItem(item);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    /**
     * Bluetooth LE
     */
    private class BleReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int action_type  = intent.getIntExtra(BleService.ACTION_ID, -1);
            switch (action_type){
                case BleService.ACTION_GATT_CONNECTED:
                    bleConnectionHeader.setStatusGone();
                    mainFragment.updateBleConnection(MainFragment.BLE_CONNECTED);

                    if(bleService.getDevice() != null){
                        bleDevicesAdapter.addSelectedDevice(bleService.getDevice());
                    }

                    break;

                case BleService.ACTION_GATT_DISCONNECTED:
                    bleConnectionHeader.setStatusGone();
                    mainFragment.updateBleConnection(MainFragment.BLE_DISCONNECTED);
                    bleDevicesAdapter.setSelectedIndex(BleListAdapter.NO_SELECTION);
                    break;

                case BleService.ACTION_UPDATE_UI:
                    mainFragment.updateContent();
                    break;

                case BleService.ACTION_UPDATE_UUID_UI:
                    mainFragment.updateBleConnection(MainFragment.BLE_CONNECTED);
                    break;
            }

        }

    }

    private class MicrochipBleConnection implements ServiceConnection{
        public void onServiceConnected(ComponentName className, IBinder binder) {
            BleService.MicrochipBinder b = (BleService.MicrochipBinder) binder;
            bleService = b.getService();
            bleService.initialize(MainActivity.this);
            attemptReconnect();
        }

        public void onServiceDisconnected(ComponentName className) {
            bleService = null;
        }
    }

    private void attemptReconnect() {
        if(bleService != null && bleService.isInitialized()){
            String address = DataStore.getBleDeviceMacAddress(MainActivity.this);
            if(!address.isEmpty())
                bleService.connect(address);
        }
    }

    @Override
    public void onBleDisabled() {
        checkStartBleScanning();
    }

    @Override
    public void onBleScan(BluetoothDevice device) {
        Bundle b = new Bundle();
        b.putString(DEVICE, GGson.toJson(device));

        Message m = new Message();
        m.what = ADD_DEVICE;
        m.setData(b);

        handler.sendMessage(m);
    }

    @Override
    public void onBleScanFailed(String message){
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onScanningStarted() {
        bleConnectionHeader.setStatusScanning();
    }

    @Override
    public void onScanningStopped() {
        bleConnectionHeader.setStatusGone();
    }

    @Override
    public void onConnectingToDevice() {
        bleConnectionHeader.setStatusConnecting();
        mainFragment.updateBleConnection(MainFragment.BLE_CONNECTING);
    }

    private void checkStartBleScanning() {
        if(bleService != null && !bleService.isBluetoothEnabled()){
            if(!showingBluetoothDialog){
                showingBluetoothDialog = true;
                new MaterialDialog.Builder(this)
                        .title(getString(R.string.settings_bluetooth))
                        .content(getString(R.string.settings_bluetooth_message))
                        .positiveText(getString(R.string.yes))
                        .negativeText(getString(R.string.no))
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);

                                Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);
                                dialog.dismiss();
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                super.onNegative(dialog);
                                dialog.dismiss();
                            }
                        })
                        .dismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                showingBluetoothDialog = false;
                            }
                        })
                        .show();
            }
        }else{
            startScanning();
        }
    }

    public void startScanning(){
        if(bleService != null){
            verifyBleServiceIsInitialized();
            bleService.startScanning();
        }
    }

    private void stopScanning(){
        if(bleService != null){
            verifyBleServiceIsInitialized();
            bleService.stopScanning();
        }
    }

    private void verifyBleServiceIsInitialized() {
        if(!bleService.isInitialized()){
            bleService.initialize(this);
        }
    }

    /**
     * Other
     */
    private void startJob(int type){
        handler.removeMessages(type);
        sendJobDelayedMessage(type, 0);
    }

    private void sendJobDelayedMessage(int type, int delay) {
        Message message = new Message();
        message.what = type;
        handler.sendMessageDelayed(message, delay);
    }

    private void stopJob(int type) {
        handler.removeMessages(type);
        fetching = false;
    }

}