package geometry.lines;

import com.esri.core.geometry.Line;
import io.github.unterstein.botlogic.services.StoredPricesService;
import io.github.unterstein.persistent.entity.StoredPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

@Component
public class LinesCreator {

    @Autowired
    private StoredPricesService storedPricesService;

//    public Line createHallUpLine(Long period){
//        LinkedList<StoredPrice> prices = storedPricesService.getStoredPricesPortion(period);
//        StoredPrice firstMax = pollMax(prices);
//        StoredPrice secondMax = pollMax(prices);
//
//        return new Line(firstMax.getPrice(), firstMax.getId(),
//                        secondMax.getPrice(), secondMax.getId());
//    }

    private StoredPrice pollMax(LinkedList<StoredPrice> prices){
        double max = prices.stream().mapToDouble(StoredPrice::getPrice).max().orElse(0.0);
        StoredPrice maxStored = prices.stream().filter(price -> price.getPrice().equals(max)).findFirst().orElse(new StoredPrice(0.0));
        prices.remove(maxStored);
        return maxStored;
    }

    public Line predictLineAfterMinutes(Line line, long minutesAfter){

        return line;
    }
}
