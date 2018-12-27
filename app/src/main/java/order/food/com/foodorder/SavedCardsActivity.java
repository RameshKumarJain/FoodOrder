package order.food.com.foodorder;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SavedCardsActivity extends AppCompatActivity {

    ListView savedCardList;

    SavedCardDatabaseAdapter savedCardDatabaseAdapter;

    SavedCardCustomAdapter customAdapter;
    TextView messageTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_cards);

        initializeToolBar();

        initializeViews();

        savedCardDatabaseAdapter = new SavedCardDatabaseAdapter(this);

        Cursor c = savedCardDatabaseAdapter.getSavedCards(HomeActivity.userId);


        customAdapter = new SavedCardCustomAdapter(c);

        savedCardList.setAdapter(customAdapter);

    }

    private void initializeViews() {
        messageTxt = findViewById(R.id.message_txt);

        savedCardList = findViewById(R.id.saved_cards_list);
    }

    private void initializeToolBar() {
        Toolbar toolbar = findViewById(R.id.saved_cards_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_arrow_back_black_24dp));

        toolbar.setNavigationOnClickListener((v) -> onBackPressed());
    }

    class SavedCardCustomAdapter extends BaseAdapter {

        Cursor cursor;

        public SavedCardCustomAdapter(Cursor c) {
            cursor = c;
        }

        @Override
        public int getCount() {
            if (cursor.getCount() == 0) {
                messageTxt.setVisibility(View.VISIBLE);
            } else {
                messageTxt.setVisibility(View.GONE);
            }
            return cursor.getCount();
        }

        @Override
        public Object getItem(int position) {
            return cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstant.ID));
        }

        @Override
        public long getItemId(int position) {
            return R.id.card_no_txt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView cardNoTextView;
            ImageView deleteCard;

            convertView = getLayoutInflater().inflate(R.layout.saved_card_view, null);

            cardNoTextView = (TextView) convertView.findViewById(R.id.card_no_txt);
            deleteCard = (ImageView) convertView.findViewById(R.id.delete_card_img);

            cursor.moveToPosition(position);

            deleteCard.setOnClickListener(v -> {
                savedCardDatabaseAdapter.delete(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstant.ID)));
                cursor = savedCardDatabaseAdapter.getSavedCards(getSharedPreferences(getString(R.string.food_order), MODE_PRIVATE).getInt(SavedCardDatabaseAdapter.USERID, 0));
                customAdapter.notifyDataSetChanged();
            });

            String cardNo = cursor.getString(cursor.getColumnIndexOrThrow(SavedCardDatabaseAdapter.CARDNUMBER));
            cardNoTextView.setText(cardNo.substring(0, 4) + " X X X X X X X X " + cardNo.substring(12));

            return convertView;
        }
    }

    @Override
    protected void onDestroy() {
        RestaurantBasketDetails.saveRestaurantDetailsToSharedPreferences(getSharedPreferences(getString(R.string.food_order), MODE_PRIVATE));
        super.onDestroy();
    }

}
