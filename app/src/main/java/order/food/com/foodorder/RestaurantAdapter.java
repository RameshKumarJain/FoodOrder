package order.food.com.foodorder;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;


class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {
    public static final String FLAT = "Flat ";
    public static final String PERCENT_OFF = "% OFF";
    public static final String ABOVE = " above ";
    public static final String OPEN = "Open";
    public static final String CLOSES_BY = "Closes by ";
    public static final String CLOSED = "Closed.";
    public static final String YET_TO_OPEN = "Yet to open.";
    Cursor restaurantCursor;
    Context context;
    boolean isLandscape;
    int position = 0;

    public RestaurantAdapter(Cursor cursor, Context context, boolean isLandscape) {
        restaurantCursor = cursor;
        this.context = context;
        this.isLandscape = isLandscape;
    }

    public void swapCursor(Cursor newCursor) {
        if (newCursor == restaurantCursor) {
            return;
        }

        if (newCursor != null) {
            restaurantCursor = newCursor;
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            notifyItemRangeRemoved(0, getItemCount());
            restaurantCursor = null;
        }
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View contentView = null;
        if (isLandscape) {
            contentView = LayoutInflater.from(context).inflate(R.layout.restaurant_view_landscape, null);
        } else {
            contentView = LayoutInflater.from(context).inflate(R.layout.restaurant_view, null);
        }
        return new RestaurantViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder restaurantViewHolder, int i) {
        restaurantCursor.moveToPosition(i);
        restaurantViewHolder.res_name.setText(restaurantCursor.getString(restaurantCursor.getColumnIndexOrThrow(RestaurantDatabaseAdapter.NAME_FIELD)));
        setRatingText(restaurantViewHolder.res_rating);
        String restaurantStatus = getRestaurantStatus();
        restaurantViewHolder.res_status.setText(restaurantStatus);
        if (restaurantStatus.contains(CLOSED) || restaurantStatus.contains(YET_TO_OPEN)) {
            restaurantViewHolder.currentView.setEnabled(false);
            restaurantViewHolder.currentView.setAlpha(.5f);
        } else {
            restaurantViewHolder.currentView.setEnabled(true);
            restaurantViewHolder.currentView.setAlpha(1.0f);
        }
        setOfferString(restaurantViewHolder.res_offer, restaurantCursor);
        setVegOrNonVegIcon(restaurantViewHolder.res_veg_logo);
        String imgName = restaurantCursor.getString(restaurantCursor.getColumnIndexOrThrow(RestaurantDatabaseAdapter.IMAGE_FIELD));
        loadBitmap(imgName, restaurantViewHolder.res_logo);
        restaurantViewHolder.id = restaurantCursor.getInt(restaurantCursor.getColumnIndexOrThrow(DatabaseConstant.ID));
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RestaurantViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        position = holder.getAdapterPosition();
    }

    private void setRatingText(TextView res_rating) {
        double ratingVal = Double.valueOf(restaurantCursor.getDouble(restaurantCursor.getColumnIndexOrThrow(RestaurantDatabaseAdapter.OVERALL_RATING_FIELD)));
        res_rating.setText(String.valueOf(ratingVal) + "★");
        if (ratingVal > 3.5) {
            res_rating.setBackground(context.getResources().getDrawable(R.color.rating_good, null));
        } else if (ratingVal > 2.5) {
            res_rating.setBackground(context.getResources().getDrawable(R.color.rating_average, null));
        } else {
            res_rating.setBackground(context.getResources().getDrawable(R.color.rating_bad, null));
        }
    }

    private void setVegOrNonVegIcon(ImageView res_veg_logo) {
        int isVeg = restaurantCursor.getInt(restaurantCursor.getColumnIndexOrThrow(RestaurantDatabaseAdapter.IS_VEG_FIELD));
        if (isVeg == 0) {
            res_veg_logo.setBackground(context.getResources().getDrawable(R.drawable.veg_logo, null));
        } else {
            res_veg_logo.setBackground(context.getResources().getDrawable(R.drawable.nv_logo, null));
        }
    }

    public static void setOfferString(TextView res_offer, Cursor restaurantCursor) {

        int offerPercent = restaurantCursor.getInt(restaurantCursor.getColumnIndexOrThrow(RestaurantDatabaseAdapter.OFFER_PERCENTAGE_FIELD));
        double minOfferPrice = restaurantCursor.getDouble(restaurantCursor.getColumnIndexOrThrow(RestaurantDatabaseAdapter.MIN_OFFER_PRICE_FIELD));
        if (offerPercent != 0) {
            if (minOfferPrice > 0.0) {
                res_offer.setText(FLAT + offerPercent + PERCENT_OFF + ABOVE + minOfferPrice);

            } else {
                res_offer.setText(FLAT + offerPercent + PERCENT_OFF);
            }
            res_offer.setVisibility(View.VISIBLE);
        } else {
            res_offer.setVisibility(View.GONE);
        }
    }

    private String getRestaurantStatus() {
        String opensAt = restaurantCursor.getString(restaurantCursor.getColumnIndexOrThrow(RestaurantDatabaseAdapter.OPENS_AT_FIELD));
        String closesAt = restaurantCursor.getString(restaurantCursor.getColumnIndexOrThrow(RestaurantDatabaseAdapter.CLOSES_FIELD));

        Calendar cal = Calendar.getInstance();
        int hr = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        try {
            int startTime = Integer.parseInt(opensAt.replace(":", ""));
            int closeTime = Integer.parseInt(closesAt.replace(":", ""));
            int actualDate = (hr * 100 + min);
            if (actualDate > startTime) {
                if (actualDate < closeTime) {
                    if (closeTime - actualDate > 100) {
                        return OPEN;
                    } else {
                        return CLOSES_BY + closesAt;
                    }

                } else {
                    return CLOSED + opensAt;
                }

            } else {
                return YET_TO_OPEN;
            }
        } catch (Exception e) {

        }
        return "";
    }

    @Override
    public int getItemCount() {
        return restaurantCursor.getCount();
    }

    public void loadBitmap(String imageName, ImageView imageView) {

        final Bitmap bitmap = LRUCacheLoader.getBitmapFromMemCache(imageName);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.ic_image_black_24dp);
            BitMapLoader task = new BitMapLoader(imageView, imageName, context.getAssets());
            task.execute();
        }
    }
}

class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView res_name, res_rating, res_status, res_offer;
    ImageView res_logo, res_veg_logo;
    View currentView;
    int id;

    public RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);
        currentView = itemView;
        res_name = (TextView) itemView.findViewById(R.id.restaurant_name_txt);
        res_rating = (TextView) itemView.findViewById(R.id.rating_txt);
        res_status = (TextView) itemView.findViewById(R.id.live_status_txt);
        res_offer = (TextView) itemView.findViewById(R.id.offer_txt);
        res_logo = (ImageView) itemView.findViewById(R.id.res_img);
        res_veg_logo = (ImageView) itemView.findViewById(R.id.veg_img);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent restaurantItemIntent = new Intent(v.getContext(), RestaurantItemsActivity.class);
        restaurantItemIntent.putExtra(RestaurantItemsDatabaseAdapter.RESTAURANT_ID, this.id);
        v.getContext().startActivity(restaurantItemIntent);
    }
}


