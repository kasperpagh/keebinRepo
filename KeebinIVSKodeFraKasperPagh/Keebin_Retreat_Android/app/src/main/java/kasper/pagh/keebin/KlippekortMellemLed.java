package kasper.pagh.keebin;

import android.content.Context;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import entity.PrePaidCard;
import rest.coffeeRest.GetAllPrepaidcardVariations;



public class KlippekortMellemLed implements AsyncResponse{
    private List<PrePaidCard> prepaid;
    private Gson gson = new Gson();

    public KlippekortMellemLed(String url, Context context) {
        prepaid = new ArrayList<PrePaidCard>();
        GetAllPrepaidcardVariations getall = new GetAllPrepaidcardVariations(url,this,context);
        getall.execute();
    }
    public List<PrePaidCard> getCardVariations(){
        return prepaid;
    }

    @Override
    public void processFinished(String output) {
        PrePaidCard[] prepaidcards = gson.fromJson(output, PrePaidCard[].class);

        for (PrePaidCard eachCard : prepaidcards)
        {
            PrePaidCard newPrepaidCard = new PrePaidCard(eachCard.getId(),eachCard.getCount(),eachCard.getPrice(),eachCard.getCents(),eachCard.getName(),eachCard.getCoffeeBrandId());

            prepaid.add(newPrepaidCard);
        }
    }
}
