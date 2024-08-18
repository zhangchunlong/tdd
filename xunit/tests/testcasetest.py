# -*- coding: utf-8 -*-

from src.testcase import WasRun, TestCase


class TestCaseTest(TestCase):
    def testTemplateMethod(self):
        test = WasRun("testMethod")
        test.run()
        assert("setUp testMethod tearDown " == test.log)


# TODO： 调用测试方法之后调用tearDown
# TODO: 即使测试方法执行识别也会调用tearDown
# TODO: 执行符合测试
# TODO：报告测试结果
# TODO: 在WasRun的日志中记录调用串

TestCaseTest("testTemplateMethod").run()
