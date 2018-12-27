package order.food.com.foodorder;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class AddFilterFragment extends BottomSheetDialogFragment implements DialogInterface.OnDismissListener {

    ListView cuisineList, rapidFilerList;

    static SparseBooleanArray savedCuisineId, savedRapidId;

    Button applyFilter, clearFilter;
    Toolbar toolbar;

    ArrayAdapter<String> cuiniseAdapter, rapidFilterAdapter;

    public static String[] cuisineValues = new String[]{"Briyani", "Chats", "Chinese", "Desserts", "Ice Creams", "North Indian",
            "Pizza", "South Indian"};
    public static String[] rapidFilterValues = new String[]{"Offer Discount", "Veg Only"};

    public static AddFilterFragment newInstance(Toolbar toolbar) {
        AddFilterFragment addFilterFragment = new AddFilterFragment();
        addFilterFragment.toolbar = toolbar;
        return addFilterFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.filter_layout, container,
                false);

        initializeViews(view);


        initializeCuiniseAdapterAndList(view);

        initializeRapidFilterAdapterAndList(view);

        initializeFilterListeners();

        loadTheSavedFilterState();

        return view;

    }

    private void initializeRapidFilterAdapterAndList(View view) {
        rapidFilterAdapter = new ArrayAdapter<>(view.getContext(),
                android.R.layout.simple_list_item_multiple_choice, rapidFilterValues);

        rapidFilerList.setAdapter(rapidFilterAdapter);
        rapidFilerList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    private void initializeCuiniseAdapterAndList(View view) {
        cuiniseAdapter = new ArrayAdapter<>(view.getContext(),
                android.R.layout.simple_list_item_multiple_choice, cuisineValues);

        cuisineList.setAdapter(cuiniseAdapter);
        cuisineList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    private void initializeFilterListeners() {
        applyFilter.setOnClickListener((v) -> {
            savedCuisineId = cuisineList.getCheckedItemPositions();
            savedRapidId = rapidFilerList.getCheckedItemPositions();
            callbackTheActivityDialogDismissed();
            this.dismiss();
        });
        clearFilter.setOnClickListener((v) -> {
            if (savedCuisineId != null)
                savedCuisineId.clear();
            if (savedRapidId != null)
                savedRapidId.clear();
            cuisineList.setAdapter(cuiniseAdapter);
            rapidFilerList.setAdapter(rapidFilterAdapter);
            callbackTheActivityDialogDismissed();
            this.dismiss();
        });
    }

    private void loadTheSavedFilterState() {
        for (int i = 0; savedCuisineId != null && i < savedCuisineId.size(); i++) {
            cuisineList.setItemChecked(savedCuisineId.keyAt(i), savedCuisineId.valueAt(i));
        }
        for (int i = 0; savedRapidId != null && i < savedRapidId.size(); i++) {
            rapidFilerList.setItemChecked(savedRapidId.keyAt(i), savedRapidId.valueAt(i));
        }
    }

    private void initializeViews(View view) {
        cuisineList = view.findViewById(R.id.list_cuisine);
        rapidFilerList = view.findViewById(R.id.list_rapid_filter);
        applyFilter = view.findViewById(R.id.btn_apply_filter);
        clearFilter = view.findViewById(R.id.btn_clear_filter);
    }

    public void callbackTheActivityDialogDismissed() {
        if (getActivity() != null && getActivity() instanceof Dismissed) {
            ((Dismissed) getActivity()).dialogDismissed();
        }
    }

    public static String getCuisineValues() {
        if (savedCuisineId == null) {
            return null;
        }
        StringBuilder result = new StringBuilder("(");
        int i;
        for (i = 0; i < cuisineValues.length; i++) {
            if (savedCuisineId.get(i)) {
                result.append("'" + cuisineValues[i] + "', ");
            }
        }
        if (result.toString().equals("(")) {
            return null;
        }
        result.append(")");
        String finalResult = result.toString();
        if (finalResult.contains(", )")) {
            finalResult = finalResult.replace(", )", " )");
        }
        return finalResult;
    }

    public interface Dismissed {
        void dialogDismissed();
    }
}
