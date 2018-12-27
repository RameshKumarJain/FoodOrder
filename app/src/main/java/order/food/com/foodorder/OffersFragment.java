package order.food.com.foodorder;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


public class OffersFragment extends Fragment {

    OfferDatabaseAdapter offerDatabaseAdapter;

    OfferAdapter offerAdapter;
    ListView offerViewList;

    int position;

    public static OffersFragment newInstance(int position) {
        OffersFragment offersFragment = new OffersFragment();
        offersFragment.position = position;
        return offersFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View offerView = inflater.inflate(R.layout.fragment_offers, container, false);
        offerViewList = (ListView) offerView.findViewById(R.id.offers_list);
        offerDatabaseAdapter = new OfferDatabaseAdapter(this.getContext());
        Cursor c = offerDatabaseAdapter.getOffers();
        offerAdapter = new OfferAdapter(c, this.getContext());
        offerViewList.setAdapter(offerAdapter);
        if (position != -1) {
            offerViewList.smoothScrollToPosition(position);
        }
        return offerView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    class OfferAdapter extends BaseAdapter {
        Cursor cursor;
        Context context;

        public OfferAdapter(Cursor c, Context context) {
            cursor = c;
            this.context = context;
        }

        @Override
        public int getCount() {
            return cursor.getCount();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView offerImage;
            TextView offerName, description, validTill, offerCode, termsAndConditions;
            convertView = getLayoutInflater().inflate(R.layout.offer_view, null);
            offerName = convertView.findViewById(R.id.offer_name);
            description = convertView.findViewById(R.id.offer_description);
            validTill = convertView.findViewById(R.id.validTill);
            offerCode = convertView.findViewById(R.id.offer_code_txt);
            termsAndConditions = convertView.findViewById(R.id.terms_and_condition);
            offerImage = convertView.findViewById(R.id.offer_img);
            cursor.moveToPosition(position);
            offerName.setText(cursor.getString(cursor.getColumnIndexOrThrow(OfferDatabaseAdapter.OFFER_NAME)));
            description.setText(cursor.getString(cursor.getColumnIndexOrThrow(OfferDatabaseAdapter.DESCRIPTION)));
            validTill.setText("Valid upto: " + cursor.getString(cursor.getColumnIndexOrThrow(OfferDatabaseAdapter.VALID_TILL)));
            offerCode.setText(cursor.getString(cursor.getColumnIndexOrThrow(OfferDatabaseAdapter.OFFER_CODE)));
            termsAndConditions.setText("Terms and conditions:\n\n" + cursor.getString(cursor.getColumnIndexOrThrow(OfferDatabaseAdapter.TERMS_AND_CONDITIONS)));
            offerCode.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("offer_code", offerCode.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, offerCode.getText().toString() + " copied to clipboard.", Toast.LENGTH_SHORT).show();
            });
//            convertView.setOnClickListener(v -> {
//                if (termsAndConditions.getVisibility() == View.GONE) {
//                    termsAndConditions.setVisibility(View.VISIBLE);
//                    return;
//                }
//                termsAndConditions.setVisibility(View.GONE);
//            });
            try {
                offerImage.setImageBitmap(BitmapFactory.decodeStream(
                        context.getAssets().open(cursor.getString(cursor.getColumnIndexOrThrow(OfferDatabaseAdapter.IMAGE_NAME)))));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return convertView;
        }
    }
}
