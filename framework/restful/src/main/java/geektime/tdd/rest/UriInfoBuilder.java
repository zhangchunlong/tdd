package geektime.tdd.rest;

interface UriInfoBuilder {

    Object getLastMatchedResource();

    void addMatchedResource(Object resource);
}
