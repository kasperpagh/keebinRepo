package adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import entity.OrderItem;
import kasper.pagh.keebin.MainActivity;
import kasper.pagh.keebin.OrderChangeListener;
import kasper.pagh.keebin.R;

/**
 * Created by kaspe on 12-05-2017.
 */

public class MenuListAdapter extends ArrayAdapter<OrderItem>
{
    Context context;
    List<OrderItem> menuList;
    int quantity;
    private OrderChangeListener delegate;

    TextView priceView;
    TextView nameView;
    TextView quatityOfItemsView;

    TextView addItemBtn;
    TextView subtractItemBtn;

    public MenuListAdapter(Context context, OrderChangeListener delegate, List<OrderItem> menuList)
    {
        super(context, R.layout.order_row, menuList);
        this.context = context;
        this.delegate = delegate;
        this.menuList = menuList;
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        final View viewRow = inflater.inflate(R.layout.order_row, parent, false);
        if (position % 2 == 0)
        {
            viewRow.setBackgroundColor(context.getResources().getColor(R.color.everySecondViewRow));
        }


        priceView = (TextView) viewRow.findViewById(R.id.priceTag);
        nameView = (TextView) viewRow.findViewById(R.id.itemName);
        quatityOfItemsView = (TextView) viewRow.findViewById(R.id.numberOfSelectedItems);

        addItemBtn = (TextView) viewRow.findViewById(R.id.addItem);
        subtractItemBtn = (TextView) viewRow.findViewById(R.id.subtractItem);

        priceView.setText(menuList.get(position).getPriceKroner() + "," + menuList.get(position).getPriceOre() + " ,-");
        nameView.setText(menuList.get(position).getCoffeeKindName());

        addItemBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (MainActivity.globalOrderList.isEmpty())
                {
                    OrderItem order = menuList.get(position);
                    order.setQuantity(1);
                    MainActivity.globalOrderList.add(order);
                    delegate.updateList(position);
                }
                else
                {
                    for (int i = 0; i < MainActivity.globalOrderList.size(); i++)
                    {
                        OrderItem oi = MainActivity.globalOrderList.get(i);

                        if (oi.getCoffeeKindName().equalsIgnoreCase(menuList.get(position).getCoffeeKindName()))
                        {
                            oi.setQuantity(oi.getQuantity() + 1);
                            delegate.updateList(position);
                        }
                        else
                        {
                            if (i == MainActivity.globalOrderList.size() - 1)
                            {
                                OrderItem order = menuList.get(position);
                                order.setQuantity(order.getQuantity() - order.getQuantity());
                                MainActivity.globalOrderList.add(order);
                                delegate.updateList(position);
                            }
                        }

                    }
                }
            }
        });

        subtractItemBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (MainActivity.globalOrderList.isEmpty() || MainActivity.globalOrderList == null)
                {
                    return;
                }

                for (int i = 0; i < MainActivity.globalOrderList.size(); i++)
                {
                    OrderItem oi = MainActivity.globalOrderList.get(i);

                    if (oi.getQuantity() == 1)
                    {
                        MainActivity.globalOrderList.get(i).setQuantity(0);
                        delegate.updateList(position);
                        MainActivity.globalOrderList.remove(i);
                    }
                    else if (oi.getCoffeeKindName().equalsIgnoreCase(menuList.get(position).getCoffeeKindName()))
                    {
                        if (MainActivity.globalOrderList.get(i).getQuantity() != 0 && MainActivity.globalOrderList.get(i).getQuantity() > 0 && !MainActivity.globalOrderList.isEmpty())
                        {
                            MainActivity.globalOrderList.get(i).setQuantity(MainActivity.globalOrderList.get(i).getQuantity() - 1);
                            delegate.updateList(position);
                        }

                    }

                }
            }
        });


        return viewRow;
    }
}
