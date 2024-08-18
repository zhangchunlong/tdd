# -*- coding: utf-8 -*-

from src.testcase import WasRun, TestCase


class TestCaseTest(TestCase):
    def testRunning(self):
        test = WasRun("testMethod")
        assert(not test.wasRun)
        test.run()
        assert(test.wasRun)

# TODO: 调用测试方法
# TODO: 调用测试方法之前调用setUp
# TODO： 调用测试方法之后调用tearDown
# TODO: 即使测试方法执行识别也会调用tearDown
# TODO: 执行符合测试
# TODO：报告测试结果


TestCaseTest("testRunning").run()
