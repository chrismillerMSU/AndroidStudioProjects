package com.example.patrick.justjava;

/**
 * IMPORTANT: Add your package below. Package name can be found in the project's AndroidManifest.xml file.
 * This is the package name our example uses:
 *
 * package com.example.android.justjava;
 *
 */


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This app displays an order form to order coffee.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    int quantity = 1;

    public void increment(View view) {
        if(quantity >= 100){
            Toast.makeText(this, "You cannot have more than 100 cups of coffee", Toast.LENGTH_SHORT).show();
            return;
        }

        quantity++;
        displayQuantity(quantity);

    }
    public void decrement(View view) {
        if(quantity <= 1) {
            Toast.makeText(this, "You cannot have less than 1 cup of coffee", Toast.LENGTH_SHORT).show();
            return;
        }
        quantity--;
        displayQuantity(quantity);

    }

    /**
     * This method is called when the order button is clicked.
     */
    public void submitOrder(View view) {
        CheckBox checkState = (CheckBox) findViewById(R.id.whipped_checked);
        Boolean whipState = checkState.isChecked();
        Log.v("MainActivity.java", whipState.toString());
        checkState = (CheckBox) findViewById(R.id.chocolate_checkbox);
        Boolean chocolateState = checkState.isChecked();
        int price = calculatePrice(whipState,chocolateState);
        EditText text = (EditText)findViewById(R.id.name_field);
        String name = text.getText().toString();
        Log.v("MainActivity", "Name: " + name);
        String priceMessage = createOrderSummary(name, price, whipState, chocolateState);
        displayMessage(priceMessage);

            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"patrick212800@hotmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Just Java order for " + name);
            intent.putExtra(Intent.EXTRA_TEXT, priceMessage);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }


    }

    /**
     * This method displays the given quantity value on the screen.
     */
    private void displayQuantity(int number) {
        TextView quantityTextView = (TextView) findViewById(R.id.quantity_text_view);
        quantityTextView.setText("" + number);
    }
    /**
     * This method displays the given price on the screen .
     */

    private void displayMessage(String message) {
        TextView orderSummaryTextView = (TextView) findViewById(R.id.order_summary_text_view);
        orderSummaryTextView.setText(message);
    }
    public int calculatePrice(boolean hasWhippedCream, boolean hasChocolate){
        return quantity * (5 + (hasWhippedCream ? 1:0) + 2*(hasChocolate ? 1 : 0));

    }
    public String createOrderSummary(String name, int price, boolean whipState, boolean chocolateState){

        return getString(R.string.order_summary_name, name) +"\nAdd whipped cream? " + whipState  + "\nAdd chocolate? " + chocolateState + "\nQuantity: "+quantity+"\nTotal: $" + price + "\n"+getString(R.string.thank_you);
    }
}