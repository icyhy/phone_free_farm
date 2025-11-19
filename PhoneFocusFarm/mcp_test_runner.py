#!/usr/bin/env python3
"""
专注农场MCP自动化测试集成
用于与AI模型协作进行智能测试分析和报告生成
"""

import json
import os
import subprocess
import time
from datetime import datetime
from pathlib import Path
import xml.etree.ElementTree as ET

class FocusFarmMCPTester:
    def __init__(self):
        self.project_root = Path("/Volumes/doc/home/Documents/2025/phone_free_farm/PhoneFocusFarm")
        self.test_results_dir = None
        self.android_home = "/Volumes/doc/home/Documents/2025/phone_free_farm/PhoneFocusFarm/tools/android-sdk"
        self.java_home = "/Volumes/doc/home/Documents/2025/phone_free_farm/PhoneFocusFarm/tools/jdk-17.jdk/Contents/Home"
        
    def setup_environment(self):
        """设置测试环境"""
        os.environ["ANDROID_HOME"] = self.android_home
        os.environ["JAVA_HOME"] = self.java_home
        os.environ["PATH"] = f"{os.environ.get('PATH', '')}:{self.android_home}/platform-tools:{self.android_home}/tools"
        
    def run_tests(self):
        """运行自动化测试"""
        print("🚀 开始专注农场MCP自动化测试")
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        self.test_results_dir = self.project_root / f"mcp_test_results_{timestamp}"
        self.test_results_dir.mkdir(exist_ok=True)
        
        # 运行shell测试脚本
        print("📱 执行测试脚本...")
        result = subprocess.run(
            ["./run_automated_tests.sh"],
            cwd=self.project_root,
            capture_output=True,
            text=True
        )
        
        print("📊 收集测试结果...")
        self.collect_test_results()
        
        print("🧠 分析测试结果...")
        analysis = self.analyze_results()
        
        print("📝 生成MCP报告...")
        self.generate_mcp_report(analysis)
        
        return analysis
    
    def collect_test_results(self):
        """收集测试结果文件"""
        # 查找最新的测试结果目录
        test_dirs = list(self.project_root.glob("test_results_*"))
        if test_dirs:
            latest_test_dir = max(test_dirs, key=os.path.getctime)
            
            # 复制重要文件
            if latest_test_dir.exists():
                for item in latest_test_dir.iterdir():
                    if item.is_file():
                        subprocess.run(["cp", str(item), str(self.test_results_dir)])
                    elif item.is_dir():
                        subprocess.run(["cp", "-r", str(item), str(self.test_results_dir)])
    
    def analyze_results(self):
        """分析测试结果"""
        analysis = {
            "timestamp": datetime.now().isoformat(),
            "test_summary": {},
            "issues_found": [],
            "recommendations": [],
            "performance_metrics": {}
        }
        
        # 分析测试报告
        reports_dir = self.test_results_dir / "reports"
        if reports_dir.exists():
            for report_file in reports_dir.rglob("*.html"):
                analysis["test_summary"]["html_reports"] = str(report_file)
            
            for report_file in reports_dir.rglob("*.xml"):
                test_results = self.parse_junit_xml(report_file)
                analysis["test_summary"].update(test_results)
        
        # 分析日志
        logcat_file = self.test_results_dir / "logcat.txt"
        if logcat_file.exists():
            log_analysis = self.analyze_logcat(logcat_file)
            analysis["issues_found"].extend(log_analysis.get("errors", []))
            analysis["performance_metrics"].update(log_analysis.get("performance", {}))
        
        # 分析屏幕截图
        screenshot_file = self.test_results_dir / "screenshot.png"
        if screenshot_file.exists():
            analysis["screenshot"] = str(screenshot_file)
        
        return analysis
    
    def parse_junit_xml(self, xml_file):
        """解析JUnit XML测试报告"""
        results = {
            "total_tests": 0,
            "passed": 0,
            "failed": 0,
            "errors": 0,
            "test_cases": []
        }
        
        try:
            tree = ET.parse(xml_file)
            root = tree.getroot()
            
            results["total_tests"] = int(root.get("tests", 0))
            results["failed"] = int(root.get("failures", 0))
            results["errors"] = int(root.get("errors", 0))
            results["passed"] = results["total_tests"] - results["failed"] - results["errors"]
            
            for testcase in root.findall(".//testcase"):
                test_case = {
                    "name": testcase.get("name", ""),
                    "classname": testcase.get("classname", ""),
                    "time": float(testcase.get("time", 0)),
                    "status": "passed"
                }
                
                if testcase.find("failure") is not None:
                    test_case["status"] = "failed"
                    test_case["failure_message"] = testcase.find("failure").get("message", "")
                elif testcase.find("error") is not None:
                    test_case["status"] = "error"
                    test_case["error_message"] = testcase.find("error").get("message", "")
                
                results["test_cases"].append(test_case)
                
        except Exception as e:
            print(f"⚠️  解析XML文件失败: {e}")
        
        return results
    
    def analyze_logcat(self, logcat_file):
        """分析logcat日志"""
        analysis = {
            "errors": [],
            "performance": {},
            "warnings": []
        }
        
        try:
            with open(logcat_file, 'r', encoding='utf-8', errors='ignore') as f:
                lines = f.readlines()
                
            for line in lines:
                line = line.strip()
                
                # 检测错误
                if "E/AndroidRuntime" in line or "FATAL EXCEPTION" in line:
                    analysis["errors"].append({
                        "type": "crash",
                        "message": line,
                        "timestamp": self.extract_timestamp(line)
                    })
                elif "E/" in line and ("Error" in line or "Exception" in line):
                    analysis["errors"].append({
                        "type": "error",
                        "message": line,
                        "timestamp": self.extract_timestamp(line)
                    })
                
                # 检测警告
                elif "W/" in line:
                    analysis["warnings"].append({
                        "type": "warning",
                        "message": line,
                        "timestamp": self.extract_timestamp(line)
                    })
                
                # 性能指标
                elif "Performance" in line or "timing" in line.lower():
                    analysis["performance"]["logs_found"] = True
        
        except Exception as e:
            print(f"⚠️  分析日志文件失败: {e}")
        
        return analysis
    
    def extract_timestamp(self, log_line):
        """从日志行提取时间戳"""
        # 简单的時間戳提取逻辑
        parts = log_line.split()
        if len(parts) >= 2:
            return f"{parts[0]} {parts[1]}"
        return "unknown"
    
    def generate_mcp_report(self, analysis):
        """生成MCP格式的测试报告"""
        report = {
            "mcp_version": "1.0",
            "test_type": "focus_farm_automated_testing",
            "analysis": analysis,
            "recommendations": self.generate_recommendations(analysis),
            "next_steps": self.generate_next_steps(analysis)
        }
        
        # 保存报告
        report_file = self.test_results_dir / "mcp_test_report.json"
        with open(report_file, 'w', encoding='utf-8') as f:
            json.dump(report, f, ensure_ascii=False, indent=2)
        
        print(f"📋 MCP测试报告已生成: {report_file}")
        return report
    
    def generate_recommendations(self, analysis):
        """生成改进建议"""
        recommendations = []
        
        # 基于分析结果生成建议
        if analysis.get("test_summary", {}).get("failed", 0) > 0:
            recommendations.append({
                "priority": "high",
                "category": "testing",
                "issue": "测试失败",
                "recommendation": "修复失败的测试用例，确保核心功能正常工作"
            })
        
        if len(analysis.get("issues_found", [])) > 0:
            recommendations.append({
                "priority": "high",
                "category": "stability",
                "issue": "发现运行时错误",
                "recommendation": "检查并修复应用中的崩溃和异常"
            })
        
        # 通用建议
        recommendations.extend([
            {
                "priority": "medium",
                "category": "testing",
                "issue": "测试覆盖率",
                "recommendation": "增加更多边界条件和异常情况的测试用例"
            },
            {
                "priority": "low",
                "category": "performance",
                "issue": "性能监控",
                "recommendation": "添加性能测试，监控内存使用和响应时间"
            }
        ])
        
        return recommendations
    
    def generate_next_steps(self, analysis):
        """生成后续步骤"""
        return [
            "修复发现的问题并重新运行测试",
            "增加更多测试用例覆盖边界条件",
            "设置持续集成自动运行测试",
            "定期运行测试监控应用质量",
            "根据测试结果优化代码质量"
        ]

def main():
    """主函数"""
    tester = FocusFarmMCPTester()
    
    try:
        # 设置环境
        tester.setup_environment()
        
        # 运行测试
        analysis = tester.run_tests()
        
        # 输出结果
        print("\n🎯 测试分析完成!")
        print(f"📁 结果保存在: {tester.test_results_dir}")
        
        # 简要结果
        summary = analysis.get("test_summary", {})
        print(f"\n📊 测试摘要:")
        print(f"   总测试数: {summary.get('total_tests', 0)}")
        print(f"   通过: {summary.get('passed', 0)}")
        print(f"   失败: {summary.get('failed', 0)}")
        print(f"   错误: {summary.get('errors', 0)}")
        
        if analysis.get("issues_found"):
            print(f"\n⚠️  发现问题: {len(analysis['issues_found'])}")
        
        print(f"\n✨ 测试完成! 请查看详细报告获取更多信息。")
        
    except Exception as e:
        print(f"❌ 测试执行失败: {e}")
        return 1
    
    return 0

if __name__ == "__main__":
    exit(main())