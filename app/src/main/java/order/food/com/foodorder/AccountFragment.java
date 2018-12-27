package order.food.com.foodorder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AccountFragment extends Fragment {

    TextView username_txt, mobno_txt;
    TextView changePassword, myOrders, savedCards, faq, logout;
    Context context;
    SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account_fragment, container, false);
        context = container.getContext();
        sharedPreferences = context.getSharedPreferences(getString(R.string.food_order), Context.MODE_PRIVATE);
        initializeViews(container, view);
        setViewValues();
        initializeListener();
        return view;
    }

    private void initializeViews(ViewGroup container, View view) {
        username_txt = view.findViewById(R.id.username);
        mobno_txt = view.findViewById(R.id.mobno);
        changePassword = view.findViewById(R.id.change_password);
        myOrders = view.findViewById(R.id.my_orders);
        savedCards = view.findViewById(R.id.saved_cards);
        faq = view.findViewById(R.id.faq);
        logout = view.findViewById(R.id.logout);
    }

    private void setViewValues() {
        username_txt.setText(sharedPreferences.getString(getString(R.string.user_name), ""));
        mobno_txt.setText(sharedPreferences.getString(UsersDatabaseAdapter.MOB_FIELD, ""));
    }

    private void initializeListener() {
        changePassword.setOnClickListener(getOnChangePasswordListener());
        logout.setOnClickListener(getOnLogoutListener());
        faq.setOnClickListener(getFAQListener());
        savedCards.setOnClickListener(getSavedCardsListener());
        myOrders.setOnClickListener(getMyOrdersListener());
    }

    @NonNull
    private View.OnClickListener getMyOrdersListener() {
        return v -> {
            Intent intent = new Intent(context, MyOrdersActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        };
    }

    @NonNull
    private View.OnClickListener getSavedCardsListener() {
        return v -> {
            Intent intent = new Intent(context, SavedCardsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        };
    }

    @NonNull
    private View.OnClickListener getFAQListener() {
        return v -> {
            Intent intent = new Intent(context, FAQActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        };
    }

    @NonNull
    private View.OnClickListener getOnLogoutListener() {
        return (v) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(FrequentRestaurantDatabaseAdapter.USERID);
            editor.remove(getString(R.string.user_name));
            editor.commit();
            RestaurantBasketDetails.saveRestaurantDetailsToSharedPreferences(context.getSharedPreferences(getString(R.string.food_order), Context.MODE_PRIVATE));
            RestaurantBasketDetails.clearForLogout();
            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            this.getActivity().finish();
        };
    }

    @NonNull
    private View.OnClickListener getOnChangePasswordListener() {
        return (v) -> {
            Intent intent = new Intent(context, ChangePasswordActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        };
    }

}
