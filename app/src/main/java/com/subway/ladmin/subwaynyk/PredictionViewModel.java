package com.subway.ladmin.subwaynyk;

import java.util.ArrayList;

/**
 * Created by ladmin on 10/06/16.
 */
public class PredictionViewModel {

    String routeId;
    boolean direction;
    Prediction prediction;
    Prediction onDeckPrediction;
    Prediction inTheHolePrediction;

    PredictionViewModel(String routeId, boolean direction){
        this.routeId = routeId;
        this.direction = direction;
    }

    public void setupWithPredictions(Prediction predictions){

//        ArrayList<Prediction> persons = …
//        Stream<Person> personsOver18 = persons.stream().filter(p -> p.getAge() > 18);
    }
//
//    func setupWithPredictions(predictions: [Prediction]!){
//
//
//        var relevantPredictions = predictions.filter({(prediction) -> Bool in
//        return prediction.direction == self.direction && prediction.route!.objectId == self.routeId
//        })
//
//        print(relevantPredictions.count)
//
//        relevantPredictions.sortInPlace { $0.secondsToArrival < $1.secondsToArrival }
//        print(relevantPredictions.count)
//
//
//        if relevantPredictions.count > 0 {
//            prediction = relevantPredictions[0]
//
//
//        }
//
//        if relevantPredictions.count > 1 {
//            onDeckPrediction = relevantPredictions[1]
//        }
//
//        if relevantPredictions.count > 2 {
//            inTheHolePrediction = relevantPredictions[2]
//
//            print(inTheHolePrediction?.timeOfArrival)
//            print(inTheHolePrediction?.direction)
//            print(inTheHolePrediction?.route?.objectId)
//        }
//    }
//
//    override func isEqual(object: AnyObject?) -> Bool {
//        if let predictionVM = object as? PredictionViewModel {
//            return self.routeId == predictionVM.routeId && self.direction == predictionVM.direction
//        }else{
//            return false
//        }
//
//    }

}



