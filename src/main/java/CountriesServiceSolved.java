import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.operators.observable.ObservableSequenceEqual;

class CountriesServiceSolved implements CountriesService {

    @Override
    public Single<String> countryNameInCapitals(Country country) {
        return Observable.just(country)
                         .map(c -> c.name.toUpperCase())
                         .firstElement()
                         .toSingle();
    }

    public Single<Integer> countCountries(List<Country> countries) {
        return Observable.fromIterable(countries).count().map(c -> c.intValue());
    }

    public Observable<Long> listPopulationOfEachCountry(List<Country> countries) {
        return Observable.fromIterable(countries).map(s -> s.getPopulation());
    }

    @Override
    public Observable<String> listNameOfEachCountry(List<Country> countries) {
        return Observable.fromIterable(countries).map(c -> c.name);
    }

    @Override
    public Observable<Country> listOnly3rdAnd4thCountry(List<Country> countries) {
        return Observable.fromIterable(countries).skip(2).take(2);
    }

    @Override
    public Single<Boolean> isAllCountriesPopulationMoreThanOneMillion(List<Country> countries) {
        return Observable.fromIterable(countries)
                         .map(country -> country.getPopulation() >= 1000000)
                         .all(c -> c == true);
    }

    @Override
    public Observable<Country> listPopulationMoreThanOneMillion(List<Country> countries) {
        return Observable.fromIterable(countries)
                         .filter(country -> country.getPopulation() >= 1000000);
    }

    @Override
    public Observable<Country> listPopulationMoreThanOneMillionWithTimeoutFallbackToEmpty(final FutureTask<List<Country>> countriesFromNetwork) {
        return Observable.fromFuture(countriesFromNetwork)
                         .flatMap(countries -> Observable.fromIterable(countries))
                         .filter(country -> country.getPopulation() >= 1000000)
                         .timeout(1,TimeUnit.SECONDS,Observable.empty());
    }

    @Override
    public Observable<String> getCurrencyUsdIfNotFound(String countryName, List<Country> countries) {
        return Observable.fromIterable(countries)
                         .filter(country -> country.getName() == countryName)
                         .map(country -> country.getCurrency())
                         .defaultIfEmpty("USD");
    }

    @Override
    public Observable<Long> sumPopulationOfCountries(List<Country> countries) {
        return Observable.fromIterable(countries)
                         .map(country -> country.getPopulation())
                         .reduce((x,y) -> x+y)
                         .toObservable();
    }

    @Override
    public Single<Map<String, Long>> mapCountriesToNamePopulation(List<Country> countries) {
        return Observable.fromIterable(countries)
                         .toMap(country -> country.getName(),country -> country.getPopulation());
    }

    @Override
    public Observable<Long> sumPopulationOfCountries(Observable<Country> countryObservable1,
                                                     Observable<Country> countryObservable2) {
        return countryObservable1.mergeWith(countryObservable2)
                                 .map(country -> country.getPopulation())
                                 .reduce((x,y)->x+y)
                                 .toObservable();
    }

    @Override
    public Single<Boolean> areEmittingSameSequences(Observable<Country> countryObservable1,
                                                    Observable<Country> countryObservable2) {
        return Observable.sequenceEqual(countryObservable1,countryObservable2);
    }
}
