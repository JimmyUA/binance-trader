package geometry.prediction.situation;

import org.springframework.stereotype.Component;

@Component
public class SituationDetector {

    private  Integer upStartIndex;
    private  Integer upDefiningIndex;
    private  Integer bottomStartIndex;
    private  Integer bottomDefiningIndex;

    public PointsChainSituation detectSituation(Integer upStartIndex, Integer upDefiningIndex,
                                                Integer bottomStartIndex, Integer bottomDefiningIndex) {

        this.upStartIndex = upStartIndex;
        this.upDefiningIndex = upDefiningIndex;

        this.bottomStartIndex = bottomStartIndex;
        this.bottomDefiningIndex = bottomDefiningIndex;


        if(isPlatoSituation()){
            return PointsChainSituation.PLATO;
        } else if (isFrontBladeSituation()){
            return PointsChainSituation.FRONT_BLADE;
        } else {
            return PointsChainSituation.BACK_BLADE;
        }
    }

    private boolean isFrontBladeSituation() {
        return isBottomStartBetweenUpPoints() && !isBottomDefiningBetweenUpPoints();
    }

    private boolean isPlatoSituation() {

        return  isBottomStartBetweenUpPoints() && isBottomDefiningBetweenUpPoints();
    }

    private boolean isBottomDefiningBetweenUpPoints() {
        return bottomDefiningIndex > upStartIndex && bottomDefiningIndex < upDefiningIndex;
    }

    private boolean isBottomStartBetweenUpPoints() {
        return bottomStartIndex > upStartIndex && bottomStartIndex < upDefiningIndex;
    }
}
