class TestCase(object):
    def __init__(self, name):
        self.name = name

    def run(self, result):
        result.testStarted()

        try:
            self.setUp()
            method = getattr(self, self.name)
            method()
        except:
            result.testFailed()

        self.tearDown()

    def setUp(self):
        pass

    def tearDown(self):
        pass


class TestResult(object):
    def __init__(self):
        self.runCount = 0
        self.errorCount = 0

    def testStarted(self):
        self.runCount = self.runCount + 1

    def testFailed(self):
        self.errorCount = self.errorCount + 1

    def summary(self):
        return "%d run, %d failed" % (self.runCount, self.errorCount)


class TestSuite(object):
    def __init__(self):
        self.tests = []

    def add(self, test):
        self.tests.append(test)

    def run(self, result):
        for test in self.tests:
            test.run(result)
