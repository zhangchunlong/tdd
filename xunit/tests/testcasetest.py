# -*- coding: utf-8 -*-

from src.testcase import WasRun, TestCase


class TestCaseTest(TestCase):
    def setUp(self):
        self.test = WasRun("testMethod")

    def testRunning(self):
        self.test.run()
        assert(self.test.wasRun)

    def testSetup(self):
        self.test.run()
        assert(self.test.wasSetUp)


# TODO： 调用测试方法之后调用tearDown
# TODO: 即使测试方法执行识别也会调用tearDown
# TODO: 执行符合测试
# TODO：报告测试结果


TestCaseTest("testRunning").run()
TestCaseTest("testSetup").run()
