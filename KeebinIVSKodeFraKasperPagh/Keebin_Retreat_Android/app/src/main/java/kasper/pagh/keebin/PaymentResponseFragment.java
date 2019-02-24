package kasper.pagh.keebin;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

import dk.danskebank.mobilepay.sdk.CaptureType;
import dk.danskebank.mobilepay.sdk.Country;
import dk.danskebank.mobilepay.sdk.MobilePay;
import dk.danskebank.mobilepay.sdk.ResultCallback;
import dk.danskebank.mobilepay.sdk.model.FailureResult;
import dk.danskebank.mobilepay.sdk.model.Payment;
import dk.danskebank.mobilepay.sdk.model.SuccessResult;
import entity.OrderItem;

/**
 * Created by kaspe on 22-05-2017.
 */

public class PaymentResponseFragment extends Fragment
{
    private int MOBILEPAY_PAYMENT_REQUEST_CODE = 1337;
    private TextView resp;
    private ImageView respPic;

    public static PaymentResponseFragment newInstance(int priceKroner, int priceØre)
    {
        Bundle args = new Bundle();
        args.putInt("priceKroner", priceKroner);
        args.putInt("priceØre", priceØre);
        PaymentResponseFragment fragment = new PaymentResponseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.mobile_pay_response_view, container, false);
        resp = (TextView) view.findViewById(R.id.textMPResponse);
        Bundle arguments = getArguments();
        respPic = (ImageView) view.findViewById(R.id.pictureMPResponse);
        respPic.setVisibility(View.INVISIBLE);
        initMP();

        makePayment(arguments.getInt("priceKroner"), arguments.getInt("priceØre"));

        return view;
    }

    public void makePayment(int priceKroner, int priceØre)
    {
        boolean isMobilePayInstalled = MobilePay.getInstance().isMobilePayInstalled(getActivity());

        if (isMobilePayInstalled)
        {
            Payment payment = new Payment();

            String priceString = priceKroner + "." + priceØre;
            BigDecimal price = new BigDecimal(priceString);

            payment.setProductPrice(price);
            Log.d("PayRes", "prisen er: " + price);
            payment.setOrderId(UUID.randomUUID().toString());

            Intent paymentIntent = MobilePay.getInstance().createPaymentIntent(payment);
            startActivityForResult(paymentIntent, MOBILEPAY_PAYMENT_REQUEST_CODE);
        }
        else
        {
            Intent intent = MobilePay.getInstance().createDownloadMobilePayIntent(getActivity());
            startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MOBILEPAY_PAYMENT_REQUEST_CODE)
        {
            // The request code matches our MobilePay Intent
            MobilePay.getInstance().handleResult(resultCode, data, new ResultCallback()
            {
                @Override
                public void onSuccess(SuccessResult result)
                {
                    // The payment succeeded - you can deliver the product.
                    respPic.setImageResource(R.drawable.green_check_mark);
                    respPic.setVisibility(View.VISIBLE);
                    resp.setText("Din bestilling er godkendt");
                    MainActivity.globalOrderList = new ArrayList<OrderItem>();
                }

                @Override
                public void onFailure(FailureResult result)
                {
                    respPic.setImageResource(R.drawable.red_cross);
                    respPic.setVisibility(View.VISIBLE);
//                    link.setVisibility(View.INVISIBLE);
                    switch (result.getErrorCode())
                    {
                        case 1:
                            resp.setText("Something went wrong. Please try updating your app.");
                            break;
                        case 2:
                            resp.setText("Could not connect, please check ur internet connection.");
                            break;
                        case 3:
                            resp.setText("Your MobilePay is out of date, plz use the link to update it.");
//                            link.setVisibility(View.VISIBLE);
//                            link.setText(R.string.mobilepayGoogle);
                            break;
                        case 4:
                            resp.setText("Something went wrong. Please try updating your app using the link below.");
//                            link.setVisibility(View.VISIBLE);
//                            link.setText(R.string.keebinGooglePlay);
                            break;
                        case 5:
                            resp.setText("Something went wrong. Please try updating your app using the link below.");
//                            link.setVisibility(View.VISIBLE);
//                            link.setText(R.string.keebinGooglePlay);
                            break;
                        case 6:
                            resp.setText("Your payment has times out, please try again.");
                            break;
                        case 7:
                            resp.setText("You have exeeded your pay limit, you can view your limits under 'beløbsgrænser' on MobilePay app");
                            break;
                        case 8:
                            resp.setText("Your payment has timed out, please try again.");
                            break;
                        case 9:
                            resp.setText("Something went wrong try again later.");
                            break;
                        case 10:
                            resp.setText("Your app is out of date, please update using link below");
//                            link.setVisibility(View.VISIBLE);
//                            link.setText(R.string.keebinGooglePlay);
                            break;
                        case 11:
                            resp.setText("Order has already been sent, check for confirmation or try again later.");
                            break;
                        case 12:
                            resp.setText("Your payment have been rejectred due to suspicious behaviour. Please contact MobilePay to resolve the issue.");
                            break;
                        default:
                            resp.setText("something went wrong. Please try again later.");
                            break;
                    }

                }

                @Override
                public void onCancel()
                {
                    resp.setText("Your payment has been canceled, but you can keep shopping.");
                    // The payment was cancelled.
                }
            });
        }
    }

    public void initMP()
    {
        MobilePay.getInstance().init("APPDK0000000000", Country.DENMARK);
        //TO BE CHANGED TO RESERVE
        MobilePay.getInstance().setCaptureType(CaptureType.CAPTURE);
        MobilePay.getInstance().setTimeoutSeconds(10);
    }

}
