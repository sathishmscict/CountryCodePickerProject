package com.hbb20;

interface CountrySearchListener {
    void noSearchResult();
    void showCountryContainer();
    void showCountryContainer(CCPCountry ccpCountry);
}
