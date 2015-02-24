package android.ble.wcm.microchip.com.microchip.fragments;

import android.app.Activity;
import android.ble.wcm.microchip.com.microchip.activities.MainActivity;
import android.ble.wcm.microchip.com.microchip.ble.BleService;
import android.ble.wcm.microchip.com.microchip.provider.response.ResponseProvider;
import android.ble.wcm.microchip.com.microchip.provider.response.ResponseSelection;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.ble.wcm.microchip.com.microchip.DataStore;
import android.ble.wcm.microchip.com.microchip.R;
import android.ble.wcm.microchip.com.microchip.api.Api;
import android.ble.wcm.microchip.com.microchip.api.model.Status.Status;
import android.ble.wcm.microchip.com.microchip.model.ResponseModel;
import android.ble.wcm.microchip.com.microchip.provider.response.ResponseColumns;
import android.ble.wcm.microchip.com.microchip.provider.response.ResponseCursor;
import android.ble.wcm.microchip.com.microchip.utils.GGson;
import android.ble.wcm.microchip.com.microchip.utils.NetworkConnection;
import android.ble.wcm.microchip.com.microchip.widget.MicrochipCircleButton;
import android.ble.wcm.microchip.com.microchip.widget.MicrochipCircleStatus;
import android.ble.wcm.microchip.com.microchip.widget.MicrochipSwitch;

/**
 * Created by WillowTree on 11/4/14
 */
public class MainFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, CompoundButton.OnCheckedChangeListener {

    public static final String TAG = MainFragment.class.getSimpleName();

    private final static int RESPONSE_CALLBACK_ID = 4231;
    private static final int POTENTIOMETER_ANIM_DURATION = 1200;

    public static final int BLE_DISCONNECTED = 0;
    public static final int BLE_CONNECTED = 1;
    public static final int BLE_CONNECTING = 2;

    LinearLayout buttonLabelContainer;
    LinearLayout buttonContainer;
    LinearLayout switchesLabelContainer;
    LinearLayout switchesContainer;

    TextView buttonLabel;
    MicrochipCircleButton buttonS1;
    MicrochipCircleButton buttonS2;
    MicrochipCircleButton buttonS3;
    MicrochipCircleButton buttonS4;

    TextView switchLabel;
    MicrochipSwitch switchD1;
    MicrochipSwitch switchD2;
    MicrochipSwitch switchD3;
    MicrochipSwitch switchD4;

    TextView potentiometerLabel;
    ProgressBar potentiometer;
    TextView potentiometerText;

    LinearLayout statusServerContainer;
    MicrochipCircleStatus statusServerCircle;
    TextView statusServerText;

    LinearLayout statusBleContainer;
    MicrochipCircleStatus statusBleCircle;
    TextView statusBleText;

    int buttonsContainerX;
    float switchesLabelsContainerX;

    MainListener listener;

    float defaultPadding;

    ResponseModel currentResponse;

    // Current States
    float currentPotentiometerValue;
    boolean currentLedSwitchValue1;
    boolean currentLedSwitchValue2;
    boolean currentLedSwitchValue3;
    boolean currentLedSwitchValue4;
    boolean currentButtonValue1;
    boolean currentButtonValue2;
    boolean currentButtonValue3;
    boolean currentButtonValue4;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        getActivity().getSupportLoaderManager().initLoader(RESPONSE_CALLBACK_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main,
                container, false);
        findViews(view);

        defaultPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());

        buttonContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                buttonContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int[] position = new int[2];
                buttonContainer.getLocationOnScreen(position);
                buttonsContainerX = position[0];
            }
        });

        switchesLabelContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                switchesLabelContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int[] position = new int[2];
                switchesLabelContainer.getLocationOnScreen(position);
                switchesLabelsContainerX = position[0] + defaultPadding;

                listener.onSwitchesLabelXPosition(position[0]);
            }
        });

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    private void setContent() {

        /**
         * Remove change listener before setting content to prevent post request to be sent
         * automatically.
         */
        switchD1.setOnCheckedChangeListener(null);
        switchD2.setOnCheckedChangeListener(null);
        switchD3.setOnCheckedChangeListener(null);
        switchD4.setOnCheckedChangeListener(null);

        updateServerUI();
        updateButtonsUI();
        updatePotentiometerUI();
        updateLedSwitchesUI();

        /**
         * Re-set the check change listener to listen for user input.
         */
        switchD1.setOnCheckedChangeListener(this);
        switchD2.setOnCheckedChangeListener(this);
        switchD3.setOnCheckedChangeListener(this);
        switchD4.setOnCheckedChangeListener(this);
    }

    /**
     * Update Server Status UI
     */
    private void updateServerUI() {
        ResponseModel response = ResponseProvider.getFirstNonPostResponse(getActivity());
        if(!NetworkConnection.isConnected(getActivity())){

            statusServerCircle.setStatusOff();
            statusServerText.setText(getString(R.string.server_no_network));

        }else if(!DataStore.getAwsAmi(getActivity()).isEmpty() && response != null ) {
            switch (response.statusCode) {
                case ResponseModel.OK:
                    statusServerCircle.setStatusGreen();
                    statusServerText.setText(getString(R.string.server_configured));
                    break;

                case ResponseModel.BAD_REQUEST:
                case ResponseModel.INTERNAL_ERROR:
                    statusServerCircle.setStatusRed();
                    statusServerText.setText(getString(R.string.server_not_configured));
                    break;

                default:
                    statusServerCircle.setStatusOrange();
                    statusServerText.setText(getString(R.string.server_configuring));
            }
        }else{
            statusServerCircle.setStatusOff();
            statusServerText.setText(getString(R.string.server_not_configured));
        }
    }

    /**
     * Update BLE Device Connection Status
     *
     * @param bleStatus Connection Status
     */
    public void updateBleConnection(int bleStatus) {
        switch(bleStatus){
            case BLE_CONNECTED:
                statusBleCircle.setStatusGreen();
                statusBleText.setText(DataStore.getBleDeviceName(getActivity()) + " " + getString(R.string.ble_configured)
                        + "\n" + "UUID: " + DataStore.getBleDeviceUuid(getActivity()));
                break;

            case BLE_CONNECTING:
                statusBleCircle.setStatusOrange();
                statusBleText.setText(getString(R.string.ble_configuring));
                break;

            default:
                statusBleCircle.setStatusOff();
                statusBleText.setText(getString(R.string.ble_not_configured));
        }
    }

    /**
     * Update Buttons UI
     *
     * Priority: BLE Device > Server
     */
    private void updateButtonsUI() {
        ResponseModel response = ResponseProvider.getFirstNonPostResponse(getActivity());
        Status status = new Status();

        if(isConnectedToBleDevice()){
            if(DataStore.getBleStatus(getActivity()) != null)
                status = DataStore.getBleStatus(getActivity());
        }else if(response != null && response.responseStatus != null){
            status = GGson.fromJson(response.responseStatus, Status.class);
        }

        if(status != null){
            if(currentButtonValue1 != status.getButton1()){
                buttonS1.setSelected(status.getButton1());
                currentButtonValue1 = status.getButton1();
            }
            if(currentButtonValue2 != status.getButton2()){
                buttonS2.setSelected(status.getButton2());
                currentButtonValue2 = status.getButton2();
            }
            if(currentButtonValue3 != status.getButton3()){
                buttonS3.setSelected(status.getButton3());
                currentButtonValue3 = status.getButton3();
            }
            if(currentButtonValue4 != status.getButton4()){
                buttonS4.setSelected(status.getButton4());
                currentButtonValue4 = status.getButton4();
            }
        }else{
            currentButtonValue1 = false;
            currentButtonValue2 = false;
            currentButtonValue3 = false;
            currentButtonValue4 = false;
            buttonS1.setSelected(false);
            buttonS2.setSelected(false);
            buttonS3.setSelected(false);
            buttonS4.setSelected(false);
        }
    }

    /**
     * Update Potentiometer UI
     *
     * Priority: BLE Device > Server
     */
    private void updatePotentiometerUI() {
        ResponseModel response = ResponseProvider.getFirstNonPostResponse(getActivity());
        Status status = new Status();

        if(isConnectedToBleDevice()){
            if(DataStore.getBleStatus(getActivity()) != null)
                status = DataStore.getBleStatus(getActivity());
        }else if(response != null && response.responseStatus != null){
            status = GGson.fromJson(response.responseStatus, Status.class);
        }

        if(status != null){
            if(currentPotentiometerValue != status.getPotentiometer()){
                setPotentiometerProgress(
                        potentiometer,
                        potentiometerText,
                        currentPotentiometerValue,
                        status.getPotentiometer(),
                        POTENTIOMETER_ANIM_DURATION);
                currentPotentiometerValue = status.getPotentiometer();
            }
        }else{
            currentPotentiometerValue = 0;
            setPotentiometerProgress(
                    potentiometer,
                    potentiometerText,
                    currentPotentiometerValue,
                    currentPotentiometerValue,
                    POTENTIOMETER_ANIM_DURATION);
        }
    }

    /**
     * Update LED Switches UI
     *
     * Priority: Server > BLE Device
     */
    private void updateLedSwitchesUI() {
        ResponseModel response = ResponseProvider.getFirstNonPostResponse(getActivity());
        Status status = new Status();

        if(NetworkConnection.isConnected(getActivity()) && response != null){
            if(response.responseStatus != null){
                status = GGson.fromJson(response.responseStatus, Status.class);
            }
        }else if(isConnectedToBleDevice() && DataStore.getBleStatus(getActivity()) != null){
            status = DataStore.getBleStatus(getActivity());
        }

        if(!DataStore.getUserPostInProgress(getActivity())){

            if(status != null){
                if(currentLedSwitchValue1 != status.getLed1()){
                    switchD1.setChecked(status.getLed1());
                    currentLedSwitchValue1 = status.getLed1();
                }
                if(currentLedSwitchValue2 != status.getLed2()){
                    switchD2.setChecked(status.getLed2());
                    currentLedSwitchValue2 = status.getLed2();
                }
                if(currentLedSwitchValue3 != status.getLed3()){
                    switchD3.setChecked(status.getLed3());
                    currentLedSwitchValue3 = status.getLed3();
                }
                if(currentLedSwitchValue4 != status.getLed4()){
                    switchD4.setChecked(status.getLed4());
                    currentLedSwitchValue4 = status.getLed4();
                }
            }else{
                currentLedSwitchValue1 = false;
                currentLedSwitchValue2 = false;
                currentLedSwitchValue3 = false;
                currentLedSwitchValue4 = false;
                switchD1.setChecked(false);
                switchD2.setChecked(false);
                switchD3.setChecked(false);
                switchD4.setChecked(false);
            }
        }
    }

    /**
     * Animate layouts into position using the slideOffSet of the navigation drawer
     *
     * @param slideOffset - offset percentage from 0 to 1
     */
    public void setSlideOffset(float slideOffset){

        // Shrink and Fade Switches Labels
        float scale = ((1.0f - slideOffset) * 0.1f) + 0.9f;
        switchesLabelContainer.animate().scaleX(scale).scaleY(scale).setDuration(0).start();
        switchesLabelContainer.setAlpha(1.0f - slideOffset);

        // Fade in/out the button and switch label
        buttonLabel.setAlpha(slideOffset);
        switchLabel.setAlpha(slideOffset);

        // Animate buttons into position
        buttonContainer.animate().translationX((switchesLabelsContainerX - buttonsContainerX) * slideOffset).setDuration(0).start();
        potentiometerLabel.animate().translationX((switchesLabelsContainerX - defaultPadding) * slideOffset).setDuration(0).start();

        // Animate and fade in/out into position with slight parallax
        buttonLabelContainer.animate().translationX((switchesLabelsContainerX - buttonsContainerX) * slideOffset * 0.9f).setDuration(0).start();
        buttonLabelContainer.setAlpha(1.0f - slideOffset);

        // Scale potentiometer (half it's size) and fade in/out
        float pScale = ((1.0f - slideOffset) * 0.5f) + 0.5f;
        potentiometer.animate().scaleX(pScale).scaleY(pScale).setDuration(0).start();
        potentiometer.setAlpha(1.0f - slideOffset);

        // Animate Potentiometer into position
        potentiometerText.animate().translationX((switchesLabelsContainerX * -1/6) * slideOffset).setDuration(0).start();

        // Animate Statuses into position
        statusServerContainer.animate().translationX((switchesLabelsContainerX - defaultPadding) * slideOffset).setDuration(0).start();
        statusBleContainer.animate().translationX((switchesLabelsContainerX - defaultPadding) * slideOffset).setDuration(0).start();
    }

    /**
     * Get the first GET response
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        ResponseSelection where = new ResponseSelection();
        where.methodType(ResponseModel.GET);

        return new CursorLoader(getActivity(),
                ResponseColumns.CONTENT_URI,
                ResponseColumns.FULL_PROJECTION,
                where.sel(),
                where.args(),
                ResponseColumns.DEFAULT_ORDER + " DESC LIMIT 1");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        ResponseCursor responseCursor = new ResponseCursor(cursor);

        if(responseCursor.moveToFirst()){
            currentResponse = new ResponseModel(responseCursor);
            setContent();

            Status status = currentResponse == null || currentResponse.responseStatus == null
                    ? new Status()
                    : GGson.fromJson(currentResponse.responseStatus, Status.class);

            listener.onWriteLedToDevice(status);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    public void updateContent(){
        setContent();
    }

    /**
     * User modified the state of a checkbox
     *
     * @param buttonView Button Pressed
     * @param isChecked Boolean value
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // User modified the state of a check box
        if(!DataStore.getBleDeviceName(getActivity()).isEmpty()){
            Status status = new Status();
            status.setDeviceType(ResponseModel.DEVICE_TYPE_ANDROID);
            status.setUuid(DataStore.getBleDeviceUuid(getActivity()));
            status.setButton1(buttonS1.isSelected());
            status.setButton2(buttonS2.isSelected());
            status.setButton3(buttonS3.isSelected());
            status.setButton4(buttonS4.isSelected());
            status.setLed1(switchD1.isChecked());
            status.setLed2(switchD2.isChecked());
            status.setLed3(switchD3.isChecked());
            status.setLed4(switchD4.isChecked());
            status.setPotentiometer((int) currentPotentiometerValue);

            if(!DataStore.getAwsAmi(getActivity()).isEmpty()){
                Api.postStatus(getActivity(), status, true);
            }

            listener.onWriteLedToDevice(status);
        }
    }

    public interface MainListener{
        public void onSwitchesLabelXPosition(int x);
        public void onWriteLedToDevice(Status status);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        if(activity instanceof MainListener){
            listener = (MainListener) activity;
        }else{
            throw new ClassCastException(activity.toString() + " must implement MainListener");
        }
    }

    private void findViews(View view) {
        buttonLabelContainer = (LinearLayout) view.findViewById(R.id.buttons_label_container);
        buttonContainer = (LinearLayout) view.findViewById(R.id.buttons_container);
        switchesLabelContainer = (LinearLayout) view.findViewById(R.id.switch_label_container);
        switchesContainer = (LinearLayout) view.findViewById(R.id.switch_container);

        buttonLabel = (TextView) view.findViewById(R.id.buttons_column_label);
        buttonS1 = (MicrochipCircleButton) view.findViewById(R.id.button_1);
        buttonS2 = (MicrochipCircleButton) view.findViewById(R.id.button_2);
        buttonS3 = (MicrochipCircleButton) view.findViewById(R.id.button_3);
        buttonS4 = (MicrochipCircleButton) view.findViewById(R.id.button_4);

        switchLabel = (TextView) view.findViewById(R.id.switches_column_label);
        switchD1 = (MicrochipSwitch) view.findViewById(R.id.switch_d1);
        switchD2 = (MicrochipSwitch) view.findViewById(R.id.switch_d2);
        switchD3 = (MicrochipSwitch) view.findViewById(R.id.switch_d3);
        switchD4 = (MicrochipSwitch) view.findViewById(R.id.switch_d4);

        potentiometerLabel = (TextView) view.findViewById(R.id.potentiometer_label);
        potentiometer = (ProgressBar) view.findViewById(R.id.potentiometer_progressbar);
        potentiometerText = (TextView) view.findViewById(R.id.potentiometer_text);

        statusServerContainer = (LinearLayout) view.findViewById(R.id.status_server_container);
        statusServerCircle = (MicrochipCircleStatus) view.findViewById(R.id.status_server);
        statusServerText = (TextView) view.findViewById(R.id.status_server_text);

        statusBleContainer = (LinearLayout) view.findViewById(R.id.status_ble_container);
        statusBleCircle = (MicrochipCircleStatus) view.findViewById(R.id.status_ble);
        statusBleText = (TextView) view.findViewById(R.id.status_ble_text);
    }

    private boolean isConnectedToBleDevice(){
        BleService service = ((MainActivity) getActivity()).bleService;
        return service != null && service.isConnected();
    }
}
