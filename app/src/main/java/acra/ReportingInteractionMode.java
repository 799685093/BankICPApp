/*
 *  Copyright 2010 Emmanuel Astier & Kevin Gaudin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package acra;


/** 
* @ClassName: ReportingInteractionMode 
* @Description: TODO(定义不同的用户交互模式。 
SILENT：隐式模式，会弹出一个“强制关闭”对话框终止
应用程序以及在下次启动时自动隐式地发送报告。
TOAST：应用程序崩溃时触发一个简单的吐司，不显示强制关闭对话框。
NOTIFICATION：应用程序崩溃时触发状态栏通知，不会显示强制关闭对话框。当用户选择了 
通知，将显示一个对话框，询问他是否确定要发送报告) 
* @author 广东省电信工程有限公司信息技术研发中心 
* @date 2014-4-23 上午11:16:31 
*  
*/
enum ReportingInteractionMode {
	SILENT, NOTIFICATION, TOAST;
}