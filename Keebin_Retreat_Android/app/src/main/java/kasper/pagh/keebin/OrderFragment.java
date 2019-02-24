package kasper.pagh.keebin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import adapters.MenuListAdapter;
import entity.Order;
import entity.OrderItem;

/**
 * Created by kaspe on 12-05-2017.
 */

public class OrderFragment extends Fragment implements AsyncResponse, OrderChangeListener
{
    private ListView listView;
    private MenuListAdapter adapter;
    private List<OrderItem> menuList;
    private View viewLink;
    private ImageView mobilePayImageLink;
    private TextView mobilePayTextLink;
    private int currentFullPriceKroner = 0;
    private int currentFullPriceØre = 0;

    public OrderFragment()
    {
    }

    public static OrderFragment newInstance()
    {
        Bundle args = new Bundle();
        OrderFragment fragment = new OrderFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.order_module, container, false);
        viewLink = view;

        mobilePayImageLink = (ImageView) view.findViewById(R.id.mobilePayIconView);
        mobilePayTextLink = (TextView) view.findViewById(R.id.mobilePayTextView);

        MainActivity.globalOrderList = new ArrayList<OrderItem>();
        menuList = new ArrayList<>();


        //
        OrderItem item1 = new OrderItem(10, 123, "Mocha");
        OrderItem item2 = new OrderItem(15, 55, "Latte");
        OrderItem item3 = new OrderItem(12, 12, "Cacao");
        OrderItem item4 = new OrderItem(23, 50, "Vand");
        OrderItem item5 = new OrderItem(123, 64, "The");
        OrderItem item6 = new OrderItem(10, 123, "Bubber");
        OrderItem item7 = new OrderItem(15, 55, "Johanna");
        OrderItem item8 = new OrderItem(2356, 12, "Creme fraiche");
        OrderItem item9 = new OrderItem(23, 50, "Llama");
        OrderItem item10 = new OrderItem(5623, 64, "Hindbærbrus");


        menuList.add(item1);
        menuList.add(item2);
        menuList.add(item3);
        menuList.add(item4);
        menuList.add(item5);
        menuList.add(item6);
        menuList.add(item7);
        menuList.add(item8);
        menuList.add(item9);
        menuList.add(item10);

        //


        adapter = new MenuListAdapter(getActivity(), this, menuList);
        listView = (ListView) view.findViewById(R.id.orderItemListView);
        listView.setAdapter(adapter);
        LinearLayout ll = (LinearLayout) view.findViewById(R.id.basketOrder);
        ll.bringToFront();
        Log.d("ORDER", "onCreate er kaldt!");
        updatePrices();

        for (int i = 0; i < menuList.size(); i++)
        {
            updateList(i);
        }

        mobilePayImageLink.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                goMobilePay(currentFullPriceKroner, currentFullPriceØre);
            }
        });

        mobilePayTextLink.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                goMobilePay(currentFullPriceKroner, currentFullPriceØre);
            }
        });


        return view;
    }

    public void updatePrices()
    {
        int totalPriceKroner = 0;
        int totalPriceØre = 0;
        int vatKroner = 0;
        int vatØre = 0;
        int subTotalPriceKroner = 0; //without WAT
        int subTotalPriceØre = 0;
        TextView resultViewPrice = (TextView) viewLink.findViewById(R.id.resultViewPrice);
        TextView resultViewSubtotal = (TextView) viewLink.findViewById(R.id.resultViewSubtotal);
        TextView resultViewVat = (TextView) viewLink.findViewById(R.id.resultViewVat);

        if (!MainActivity.globalOrderList.isEmpty() && MainActivity.globalOrderList != null)
        {
            for (OrderItem oi : MainActivity.globalOrderList)
            {
                if (oi.getQuantity() > 0)
                {
                    totalPriceKroner += ((oi.getPriceKroner()) * (oi.getQuantity()));
                    totalPriceØre += ((oi.getPriceOre()) * (oi.getQuantity()));
                }
            }
            subTotalPriceKroner = totalPriceKroner - (totalPriceKroner / 5);
            subTotalPriceØre = totalPriceØre - (totalPriceØre / 5);
            vatKroner = totalPriceKroner - subTotalPriceKroner;
            vatØre = totalPriceØre - subTotalPriceØre;

            currentFullPriceKroner = totalPriceKroner;
            currentFullPriceØre = totalPriceØre;

            resultViewPrice.setText("Total: " + totalPriceKroner + "," + totalPriceØre + ",-");
            resultViewSubtotal.setText("Subtotal: " + subTotalPriceKroner + "," + subTotalPriceØre + ",-");
            resultViewVat.setText("Moms: " + vatKroner + "," + vatØre + ",-");
        }
        else
        {
            currentFullPriceKroner = 0;
            currentFullPriceØre = 0;
            resultViewPrice.setText("Total: " + 0 + "," + 0 + " ,-");
            resultViewSubtotal.setText("Subtotal: " + 0 + "," + 0 + " ,-");
            resultViewVat.setText("Moms: " + 0 + "," + 0 + " ,-");
        }
    }

    @Override
    public void processFinished(String output)
    {

    }

    public void goMobilePay(int priceKroner, int priceØre)
    {
        getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("").replace(R.id.fragment, PaymentResponseFragment.newInstance(priceKroner, priceØre)).commit();
    }

    @Override
    public void onResume()
    {
        super.onResume();

//
//        if (MainActivity.globalOrderList != null && !MainActivity.globalOrderList.isEmpty())
//        {
//            Log.d("Order", "menuList size: " + menuList.size());
//            for (OrderItem io : MainActivity.globalOrderList)
//            {
//                for (OrderItem io2 : menuList)
//                {
//                    if (io.getCoffeeKindName().equalsIgnoreCase(io2.getCoffeeKindName()))
//                    {
//                        Log.d("Order", "nanve matcher");
////                        OrderItem editedItem = io2;
//                        List<OrderItem> newList = new ArrayList<OrderItem>(menuList);
//
//                        for (OrderItem io3 : newList)
//                        {
//                            if (io.getCoffeeKindName().equalsIgnoreCase(io3.getCoffeeKindName()))
//                            {
//                                io3.setQuantity(io.getQuantity());
//                            }
//                        }
//
//                        adapter.clear();
//                        adapter.addAll(newList);
//                        adapter.notifyDataSetChanged();
//
////                        adapter.remove(io2);
////                        adapter.add(editedItem);
//                        io2.setQuantity(io.getQuantity());
//                        adapter.notifyDataSetChanged();
////                        io2.setQuantity(io.getQuantity());
//                    }
//                }
//            }
////            Log.d("Order", "Clear");
////            adapter.clear();
////            Log.d("Order", "Add: " + menuList.size());
////            adapter.addAll(menuList);
////            adapter.re
////            Log.d("Order", "Nofitfy");
////            adapter.notifyDataSetChanged();
////            Log.d("Order", "Post");
//        }
    }

    @Override
    public void updateList(int index)
    {
        View view = listView.getChildAt(index - listView.getFirstVisiblePosition());

        if (view == null)
        {
            return;
        }
        else
        {
            TextView quantityCounter = (TextView) view.findViewById(R.id.numberOfSelectedItems);
            String orderItemName = menuList.get(index).getCoffeeKindName();
            for (OrderItem io : MainActivity.globalOrderList)
            {
                if (io.getCoffeeKindName().equalsIgnoreCase(orderItemName))
                {
                    if (io.getQuantity() == 0)
                    {
                        quantityCounter.setText("" + 0);
                    }
                    else
                    {
                        int res = index - listView.getFirstVisiblePosition();
                        Log.d("Order", "opdaterer position: " + index + " minus: " + listView.getFirstVisiblePosition() + " med res: " + res);
                        quantityCounter.setText(io.getQuantity() + "");
                    }
                }
            }
        }
        updatePrices();
    }


}
