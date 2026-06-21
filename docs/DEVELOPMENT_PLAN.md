# 孕期 App 开发计划

## 1. 目标

基于 PRD，先完成 Android MVP：孕期设置与推算、首页每日建议、提醒、日历记录、趋势展示。开发过程采用本地优先、可离线运行、医学内容可追溯的架构。

## 2. 技术路线

### 2.1 推荐技术栈

- 语言：Kotlin
- UI：Jetpack Compose
- 架构：MVVM + 单向 UI 状态
- 本地数据库：Room
- 本地配置：DataStore
- 后台提醒：WorkManager + Android Notification
- 页面导航：Navigation Compose
- 依赖注入：Hilt
- 图表：Compose Canvas 自绘或轻量图表库
- 测试：JUnit、Turbine、MockK、Compose UI Test
- 构建：Gradle Kotlin DSL

### 2.2 技术选择理由

- Jetpack Compose 适合快速开发现代 Android UI。
- Room 适合结构化孕期记录和日历数据。
- DataStore 适合保存用户设置、提醒开关和轻量配置。
- WorkManager 适合可靠的本地提醒调度。
- 本地优先能降低隐私风险，并让 App 离线可用。

## 3. 工程结构

建议采用单 App 模块起步，后续再按复杂度拆模块。

```text
android-app/
  app/
    src/main/java/com/yunqi/app/
      YunqiApp.kt
      MainActivity.kt
      core/
        common/
        design/
        navigation/
        time/
      data/
        local/
        repository/
        content/
      domain/
        model/
        usecase/
        pregnancy/
      feature/
        onboarding/
        home/
        calendar/
        record/
        trends/
        settings/
        reminders/
      notification/
    src/test/
    src/androidTest/
  docs/
```

## 4. 分期计划

### Phase 0：产品与工程准备

状态：已开始。

任务：

- 完成 PRD。
- 完成开发计划。
- 确认技术栈。
- 确认 MVP 范围。
- 配置 GitHub 仓库。

交付物：

- `docs/PRD_PREGNANCY_APP.md`
- `docs/DEVELOPMENT_PLAN.md`

### Phase 1：Android 项目骨架

目标：创建可运行的 Android 空项目。

任务：

- 创建 Gradle 项目。
- 配置 Kotlin、Compose、Android Gradle Plugin。
- 创建包名：`com.yunqi.app`。
- 创建 `MainActivity`。
- 建立基础主题、颜色、排版。
- 建立底部导航骨架：首页、日历、趋势、我的。
- 增加基础 CI 检查脚本。

验收：

- Android Studio 可打开项目。
- Debug 构建通过。
- App 可在模拟器或真机启动。
- 首页显示占位内容。

### Phase 2：孕期设置与孕周计算

目标：完成核心计算闭环。

任务：

- 实现孕期档案数据模型。
- 实现孕周计算器：
  - 末次月经日期到孕周。
  - 预产期到孕周。
  - 当前孕周到预产期。
  - 孕期阶段判断。
- 实现首次使用设置页。
- 使用 DataStore 或 Room 保存孕期档案。
- 首页展示当前孕周、孕天、预产期倒计时。
- 为计算逻辑编写单元测试。

验收：

- 四种设置方式至少覆盖前三种。
- 日期边界测试通过。
- 首页能根据日期自动变化。

### Phase 3：首页建议与内容系统

目标：根据孕周展示每日提醒、饮食和运动建议。

任务：

- 建立本地内容 JSON 或 Kotlin seed 数据。
- 定义内容实体和来源字段。
- 首页按孕周读取内容。
- 实现饮食建议卡、运动建议卡、安全提醒卡。
- 增加医疗免责声明组件。
- 增加“医生限制运动”开关逻辑。

验收：

- 每个孕期阶段都有内容。
- 每条医学相关内容有来源和更新时间。
- 无网络可用。

### Phase 4：日历与记录

目标：完成记录入口和日历闭环。

任务：

- 引入 Room。
- 建立记录表：
  - CalendarEntry
  - AppointmentEntry
  - WeightEntry
  - FetalMovementEntry
  - SymptomEntry
  - ExerciseEntry
  - DietEntry
- 实现月历视图。
- 实现日期详情。
- 实现新增、编辑、删除记录。
- 实现快捷记录入口。
- 编写 Repository 和 DAO 测试。

验收：

- 日期上有记录标记。
- 各类记录可保存并回看。
- 删除有二次确认。

### Phase 5：提醒系统

目标：本地提醒可配置、可触达。

任务：

- 申请通知权限。
- 建立 ReminderRule 数据模型。
- 实现提醒设置页。
- 实现产检提醒。
- 实现体重、胎动、维生素、运动、自定义提醒。
- 使用 WorkManager 调度。
- 处理重启后的提醒恢复。

验收：

- 设置提醒后能收到本地通知。
- 修改、关闭提醒生效。
- 权限关闭时有明确引导。

### Phase 6：趋势与统计

目标：让记录产生回看价值。

任务：

- 实现体重趋势图。
- 实现胎动趋势图。
- 实现运动时长趋势图。
- 空状态引导记录。
- 支持 7 天、30 天、全部筛选。

验收：

- 2 条以上数据展示趋势。
- 空状态文案清晰。
- 图表在小屏不挤压。

### Phase 7：质量、发布与回归

目标：形成可测试、可交付的 MVP。

任务：

- 补充核心单元测试。
- 补充主要页面 Compose UI 测试。
- 完成隐私与免责声明检查。
- 完成真机冒烟测试。
- 生成 Debug APK。
- 编写发布说明。

验收：

- Debug 构建通过。
- 核心流程无阻断问题。
- 医疗边界文案完整。

## 5. 时间预估

以 1 名开发者全职节奏估算：

- Phase 0：0.5 天
- Phase 1：1 天
- Phase 2：2 天
- Phase 3：2 天
- Phase 4：4 天
- Phase 5：2 天
- Phase 6：2 天
- Phase 7：2 天

合计：约 15.5 个工作日。

如果只先做可演示 Demo，可压缩到 5-7 个工作日，减少提醒可靠性、测试覆盖和内容完整度。

## 6. 开发优先级

### 第一优先级

- 孕周推算。
- 首页。
- 日历记录。
- 产检提醒。

### 第二优先级

- 饮食和运动建议。
- 胎动记录。
- 体重趋势。
- 通知权限处理。

### 第三优先级

- 导出。
- 附件。
- 云备份。
- 家人共享。

## 7. 关键算法

### 7.1 孕周计算

输入末次月经日期：

```text
gestationalDays = today - lmpDate
week = gestationalDays / 7
day = gestationalDays % 7
dueDate = lmpDate + 280 days
```

输入预产期：

```text
lmpDate = dueDate - 280 days
gestationalDays = today - lmpDate
```

输入当前孕周：

```text
gestationalDaysAtSetup = week * 7 + day
lmpDate = setupDate - gestationalDaysAtSetup
dueDate = lmpDate + 280 days
```

### 7.2 孕期阶段

```text
0-13 周：孕早期
14-27 周：孕中期
28 周及以后：孕晚期
```

### 7.3 内容匹配

```text
currentWeek in pregnancyWeekStart..pregnancyWeekEnd
locale == userLocale
category in requestedCategories
```

## 8. 测试计划

### 8.1 单元测试

- 孕周计算。
- 预产期反推。
- 边界日期。
- 内容匹配。
- 提醒规则生成。

### 8.2 数据测试

- Room DAO 新增、编辑、删除。
- 多类型记录按日期查询。
- 趋势数据聚合。

### 8.3 UI 测试

- 首次设置流程。
- 首页展示。
- 日历新增记录。
- 记录编辑与删除。
- 提醒设置。

### 8.4 手工测试

- 通知权限。
- 系统时间变化。
- 深色模式。
- 小屏设备。
- 无网络。
- App 重启。

## 9. 风险与应对

### 9.1 医疗内容风险

风险：用户误把 App 建议当成医生诊断。

应对：

- 全局免责声明。
- 内容来源展示。
- 紧急情况引导联系医生。
- 不输出诊断结论。

### 9.2 日期计算风险

风险：孕周计算错误影响全部内容。

应对：

- 独立计算模块。
- 单元测试覆盖多种输入方式。
- 所有推算结果标注“估算”。

### 9.3 隐私风险

风险：孕期记录属于敏感个人信息。

应对：

- MVP 本地存储。
- 不上传。
- 不把敏感字段写入日志。
- 提供清除数据功能。

### 9.4 提醒可靠性风险

风险：Android 厂商后台限制导致提醒延迟。

应对：

- 使用 WorkManager。
- 关键产检事项支持用户同步到系统日历作为后续功能。
- 在 App 内展示今日提醒列表。

## 10. Git 工作流

- `main`：稳定主分支。
- `feature/docs-prd-plan`：文档分支。
- `feature/android-scaffold`：项目骨架。
- `feature/pregnancy-core`：孕期计算。
- `feature/calendar-records`：日历记录。
- `feature/reminders`：提醒。

提交信息建议：

```text
docs: add pregnancy app PRD and development plan
feat: scaffold android compose project
feat: add pregnancy date calculation
feat: add calendar records
```

## 11. 下一步开发动作

如果确认 PRD 和开发计划，下一步进入 Phase 1：

1. 检查本机 Android Studio、JDK 和 Gradle 环境。
2. 创建 Android Compose 项目。
3. 提交项目骨架。
4. 开始实现孕期设置与孕周计算。

