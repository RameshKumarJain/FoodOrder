package order.food.com.foodorder;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;

public class SelectLiveOfferActivity extends AppCompatActivity {

    ListView selectOfferList;

    OfferDatabaseAdapter offerDatabaseAdapter;

    SelectOfferCustomAdapter customAdapter;


    private String fetchOfferQuery = "SELECT offers._id, offers.offerName, offers.offerCode, offers.description, offers.discountPercentage, offers.maxDiscountPrice, offers.countAllowedPerUser, offers.paidVia, offers.imageName, coupon_history.count" +
            " FROM offers LEFT JOIN coupon_history ON offers._id = coupon_history.offerId AND coupon_history.userId = ?;";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_live_offer);

        initializeToolBar();

        initializeViews();

        offerDatabaseAdapter = new OfferDatabaseAdapter(this);

        Cursor c = offerDatabaseAdapter.executeQuery(fetchOfferQuery, new String[]{String.valueOf(HomeActivity.userId)});

        customAdapter = new SelectOfferCustomAdapter(c);

        selectOfferList.setAdapter(customAdapter);

    }

    private void initializeViews() {
        selectOfferList = (ListView) findViewById(R.id.select_offer_list);
    }

    private void initializeToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.select_offer_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_arrow_back_black_24dp));

        toolbar.setNavigationOnClickListener((v) -> onBackPressed());
    }

    class SelectOfferCustomAdapter extends BaseAdapter {

        Cursor cursor;

        public SelectOfferCustomAdapter(Cursor c) {
            cursor = c;
        }

        @Override
        public int getCount() {
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
            ImageView offerImage;
            TextView offerName, description, offerCode, offerUsedCount;
            ProgressBar offerUsedProgressBar;

            convertView = getLayoutInflater().inflate(R.layout.select_offer_view, null);

            offerName = convertView.findViewById(R.id.offer_name);
            description = convertView.findViewById(R.id.offer_description);
            offerCode = convertView.findViewById(R.id.offer_code_txt);
            offerImage = convertView.findViewById(R.id.offer_img);
            offerUsedProgressBar = convertView.findViewById(R.id.offer_progress_bar);
            offerUsedCount = convertView.findViewById(R.id.offerUsedCount);

            cursor.moveToPosition(position);

            offerName.setText(cursor.getString(cursor.getColumnIndexOrThrow(OfferDatabaseAdapter.OFFER_NAME)));
            description.setText(cursor.getString(cursor.getColumnIndexOrThrow(OfferDatabaseAdapter.DESCRIPTION)));
            offerCode.setText(cursor.getString(cursor.getColumnIndexOrThrow(OfferDatabaseAdapter.OFFER_CODE)));
            try {
                offerImage.setImageBitmap(BitmapFactory.decodeStream(
                        getAssets().open(cursor.getString(cursor.getColumnIndexOrThrow(OfferDatabaseAdapter.IMAGE_NAME)))));
            } catch (IOException e) {
                e.printStackTrace();
            }

            int allowedCount = cursor.getInt(cursor.getColumnIndexOrThrow(OfferDatabaseAdapter.COUNT_ALLOWED_PER_USER));
            int usedCount = cursor.getInt(cursor.getColumnIndexOrThrow(CouponHistoryDatabaseAdapter.COUNT));

            offerUsedProgressBar.setMax(allowedCount);
            offerUsedProgressBar.setProgress(usedCount, true);
            offerUsedProgressBar.getProgressDrawable().setColorFilter(getColor(R.color.colorAccent), android.graphics.PorterDuff.Mode.SRC_IN);
            offerUsedCount.setText(usedCount + "/" + allowedCount);
            if (usedCount >= allowedCount) {
                convertView.setAlpha(.5f);
                convertView.setBackgroundColor(getColor(R.color.text_color));
                convertView.setEnabled(false);
            } else {
                convertView.setAlpha(1.0f);
                convertView.setEnabled(true);
                convertView.setBackgroundColor(getColor(R.color.transparent));
            }
            convertView.setOnClickListener(v -> {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                returnIntent.putExtra("result", offerCode.getText());
                finish();
            });

            return convertView;
        }
    }

    @Override
    protected void onDestroy() {
        RestaurantBasketDetails.saveRestaurantDetailsToSharedPreferences(getSharedPreferences(getString(R.string.food_order), MODE_PRIVATE));
        super.onDestroy();
    }

}
