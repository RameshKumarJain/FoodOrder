package order.food.com.foodorder;

import android.content.res.Configuration;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FAQActivity extends AppCompatActivity {

    public static final String ITEMS_EXPANDED_LIST = "itemsExpandedList";
    ListView faqListView;
    FaqAdapter faqListAdapter;

    private static List<String> faqQuestionList = new ArrayList<>();
    private static List<String> faqAnswerList = new ArrayList<>();
    int[] itemsExpandedList = new int[faqQuestionList.size()];
    ;

    static {
        faqQuestionList.add("1 How should I apply offers?");
        faqQuestionList.add("2 Can I cancel my order once placed?");
        faqQuestionList.add("3 Can I track my live orders?");
        faqQuestionList.add("4 Can I pay via my Wallet/Cards?");
        faqQuestionList.add("5 Can I save and delete my credit/debit cards?");
        faqAnswerList.add("You can either copy the offer code from the offers section / select live offers while checking out your order in basket section.");
        faqAnswerList.add("No you cannot cancel your order once placed.Currently we dnt have this feature out, we will surely bring it out in upcoming version.");
        faqAnswerList.add("Yes, You can track your orders either in Accounts->My Orders, your orders will be listed with live status or you will be notified about your order in Home section.");
        faqAnswerList.add("Yes, you can pay via Credit/Debit card/ Wallets. You can also opt for COD.");
        faqAnswerList.add("Yeah, in payment section you can save the card and it will listed under Accounts->Saved Cards. You can delete the saved cards over there.");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        initializeItemExpandListIfExist(savedInstanceState);
        initializeToolBar();
        initializeFAQListAndAdapter();

    }

    private void initializeItemExpandListIfExist(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            try {
                itemsExpandedList = savedInstanceState.getIntArray(ITEMS_EXPANDED_LIST);
            } catch (Exception e) {
            }
        }
    }

    private void initializeFAQListAndAdapter() {
        faqListView = (ListView) findViewById(R.id.faq_list);
        faqListAdapter = new FaqAdapter();
        faqListView.setAdapter(faqListAdapter);
    }

    private void initializeToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.faq_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_arrow_back_black_24dp));

        toolbar.setNavigationOnClickListener((v) -> onBackPressed());
    }

    class FaqAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return faqQuestionList.size();
        }

        @Override
        public Object getItem(int position) {
            return faqQuestionList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return R.id.faq_ques_txt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView faqQuesTxt, faqAnsTxt;

            convertView = getLayoutInflater().inflate(R.layout.faq_view, null);

            faqQuesTxt = (TextView) convertView.findViewById(R.id.faq_ques_txt);
            faqAnsTxt = (TextView) convertView.findViewById(R.id.faq_ans_txt);

            faqQuesTxt.setText(faqQuestionList.get(position));
            faqAnsTxt.setText(faqAnswerList.get(position));
            if (itemsExpandedList[position] == 1) {
                faqAnsTxt.setVisibility(View.VISIBLE);
                faqQuesTxt.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp), null);
            }

            faqQuesTxt.setOnClickListener(v -> {
                if (faqAnsTxt.getVisibility() == View.GONE) {
                    faqAnsTxt.setVisibility(View.VISIBLE);
                    itemsExpandedList[position] = 1;
                    faqQuesTxt.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp), null);
                } else {
                    faqAnsTxt.setVisibility(View.GONE);
                    itemsExpandedList[position] = 0;
                    faqQuesTxt.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp), null);
                }
            });

            return convertView;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray(ITEMS_EXPANDED_LIST, itemsExpandedList);
    }

    @Override
    protected void onDestroy() {
        RestaurantBasketDetails.saveRestaurantDetailsToSharedPreferences(getSharedPreferences(getString(R.string.food_order), MODE_PRIVATE));
        super.onDestroy();
    }

}
