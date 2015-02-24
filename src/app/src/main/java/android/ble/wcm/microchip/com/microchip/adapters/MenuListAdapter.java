package android.ble.wcm.microchip.com.microchip.adapters;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import android.ble.wcm.microchip.com.microchip.R;
import android.ble.wcm.microchip.com.microchip.activities.MainActivity;
import android.ble.wcm.microchip.com.microchip.model.ResponseModel;
import android.ble.wcm.microchip.com.microchip.provider.response.ResponseProvider;
import android.ble.wcm.microchip.com.microchip.utils.ShareIntentBuilder;
import android.ble.wcm.microchip.com.microchip.view.ConsoleItem;
import android.ble.wcm.microchip.com.microchip.view.ConsoleViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: WillowTree
 * Date: 11/18/14
 * Time: 10:00 PM.
 */
public class MenuListAdapter extends RecyclerView.Adapter<ConsoleViewHolder> implements ConsoleItem.Listener {

    public List<ResponseModel> items;

    private Context context;
    private RecyclerView.LayoutManager layoutManager;
    private SparseBooleanArray mSelectedItemsIds;
    private ActionMode actionMode;

    public MenuListAdapter(Context context, RecyclerView.LayoutManager layoutManager){
        this.context = context;
        this.layoutManager = layoutManager;
        this.items = new ArrayList<ResponseModel>();
        this.mSelectedItemsIds = new SparseBooleanArray();
    }

    public void setItems(List<ResponseModel> newItems){
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    public void addItem(ResponseModel item){
        items.add(0, item);
        notifyItemInserted(0);
        layoutManager.scrollToPosition(0);
    }

    public void removeItem(int position){
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void addItems(List<ResponseModel> newItems){
        for(int i = newItems.size() - 1; i >= 0; i--){
            addItem(newItems.get(i));
        }
    }

    public void removeAllItems(){
        for(int i = items.size() - 1; i >= 0; i--){
            removeItem(i);
        }
    }

    @Override
    public ConsoleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        ConsoleItem consoleItem = new ConsoleItem(context);
        return new ConsoleViewHolder(consoleItem, this);
    }

    @Override
    public void onBindViewHolder(ConsoleViewHolder consoleViewHolder, int i) {
        ResponseModel response = items.get(i);
        int index = mSelectedItemsIds.indexOfKey(response.sha1);
        consoleViewHolder.consoleItem.setContent(response,
                index >= 0 && index < mSelectedItemsIds.size() && mSelectedItemsIds.valueAt(index));
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public void onItemClicked(final ResponseModel responseModel) {
        if(actionMode == null){
            // No Items Selected
            ((MainActivity) context).closeDrawer(new MainActivity.DrawerListener() {
                @Override
                public void onDrawerClosed() {
                    context.startActivity(ShareIntentBuilder.buildShareIntent(context, responseModel));
                }
            });
        }else{
            // Add or Remove selection for current list items
            onListItemSelect(responseModel.sha1);
        }
    }

    @Override
    public void onItemLongPressed(ResponseModel responseModel) {
        onListItemSelect(responseModel.sha1);
    }



    public boolean isEmpty(){
        return items == null || items.size() == 0;
    }

    public void toggleSelection(int sha1){
        selectView(sha1, !mSelectedItemsIds.get(sha1));
    }

    public void selectView(int sha1, boolean value){
        if(value){
            mSelectedItemsIds.put(sha1, true);
        }else{
            mSelectedItemsIds.delete(sha1);
        }
        notifyDataSetChanged();
    }

    public int getSelectedCount(){
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds(){
        return mSelectedItemsIds;
    }

    public void removeSelection(){
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    private void onListItemSelect(int sha1){
        toggleSelection(sha1);
        boolean hasCheckedItems = getSelectedCount() > 0;

        if(hasCheckedItems && actionMode == null){
            // There are some selected items, start the ActionMode
            actionMode = ((ActionBarActivity) context).startSupportActionMode(new ActionModeCallBack());
        }else if(!hasCheckedItems && actionMode != null){
            // There are no selected items, finish the ActionMode
            actionMode.finish();
        }

        if(actionMode != null){
            actionMode.setTitle(String.valueOf(getSelectedCount() + " selected"));
        }
    }

    public boolean contains(ResponseModel item) {
        for(ResponseModel r : items){
            if(r.sha1.equals(item.sha1)){
                return true;
            }
        }
        return false;
    }

    private class ActionModeCallBack implements ActionMode.Callback{

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            // Inflate contextual menu
            actionMode.getMenuInflater().inflate(R.menu.delete, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.menu_delete:
                    // Retrieve selected items and delete them out
                    SparseBooleanArray selected = getSelectedIds();
                    for(int i = (selected.size() - 1); i >= 0; i--){
                        if(selected.valueAt(i)){
                            ResponseProvider.delete(context, selected.keyAt(i));
                        }
                    }
                    setItems(ResponseProvider.getAll(context));
                    notifyDataSetChanged();
                    actionMode.finish();

                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode aMode) {
            // Remove Selected Items
            removeSelection();
            actionMode = null;
        }
    }
}
