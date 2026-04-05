@echo off
echo ========================================
echo Name-List 项目质量检查脚本
echo ========================================
echo.

REM 检查必要的配置文件
echo 1. 检查配置文件...
if exist "src\main\resources\application.properties" (
    echo   [✓] application.properties 存在
) else (
    echo   [✗] application.properties 不存在
)

if exist "src\main\resources\application-oss.properties" (
    echo   [✓] application-oss.properties 存在
) else (
    echo   [✗] application-oss.properties 不存在 (警告)
)

REM 检查文档文件
echo.
echo 2. 检查文档文件...
set doc_count=0

if exist "AGENTS.md" (
    echo   [✓] AGENTS.md 存在
    set /a doc_count+=1
) else (
    echo   [✗] AGENTS.md 不存在
)

if exist "docs\ARCHITECTURE.md" (
    echo   [✓] docs\ARCHITECTURE.md 存在
    set /a doc_count+=1
) else (
    echo   [✗] docs\ARCHITECTURE.md 不存在
)

if exist "docs\core-beliefs.md" (
    echo   [✓] docs\core-beliefs.md 存在
    set /a doc_count+=1
) else (
    echo   [✗] docs\core-beliefs.md 不存在
)

if exist "docs\quality-score.md" (
    echo   [✓] docs\quality-score.md 存在
    set /a doc_count+=1
) else (
    echo   [✗] docs\quality-score.md 不存在
)

echo   文档完整度: %doc_count%/4 文件

REM 检查必要的目录结构
echo.
echo 3. 检查目录结构...
if exist "src\main\java\org\example\namelist\controller\" (
    echo   [✓] controller 目录存在
) else (
    echo   [✗] controller 目录不存在
)

if exist "src\main\java\org\example\namelist\entity\" (
    echo   [✓] entity 目录存在
) else (
    echo   [✗] entity 目录不存在
)

if exist "src\main\java\org\example\namelist\service\" (
    echo   [✓] service 目录存在
) else (
    echo   [✗] service 目录不存在
)

if exist "src\main\java\org\example\namelist\mapper\" (
    echo   [✓] mapper 目录存在
) else (
    echo   [✗] mapper 目录不存在
)

REM 检查启动文件
echo.
echo 4. 检查启动文件...
if exist "start.bat" (
    echo   [✓] start.bat 存在
) else (
    echo   [✗] start.bat 不存在
)

if exist "mvnw" (
    echo   [✓] mvnw 存在
) else (
    echo   [✗] mvnw 不存在
)

REM 检查关键Java文件
echo.
echo 5. 检查关键Java文件...
set java_count=0

if exist "src\main\java\org\example\namelist\NameListApplication.java" (
    echo   [✓] NameListApplication.java 存在
    set /a java_count+=1
) else (
    echo   [✗] NameListApplication.java 不存在
)

if exist "src\main\java\org\example\namelist\controller\AdminController.java" (
    echo   [✓] AdminController.java 存在
    set /a java_count+=1
) else (
    echo   [✗] AdminController.java 不存在
)

if exist "src\main\java\org\example\namelist\entity\HeroPerson.java" (
    echo   [✓] HeroPerson.java 存在
    set /a java_count+=1
) else (
    echo   [✗] HeroPerson.java 不存在
)

echo   核心Java文件: %java_count%/3 文件

REM 项目编译检查
echo.
echo 6. 尝试编译项目...
echo   Maven 编译检查...(这可能需要一些时间)
mvn compile -q > compile.log 2>&1

if %ERRORLEVEL% EQU 0 (
    echo   [✓] 项目编译通过
    del compile.log
) else (
    echo   [✗] 项目编译失败，请查看 compile.log
)

REM 总结
echo.
echo ========================================
echo 检查完成
echo ========================================
echo.
echo 建议:
echo 1. 确保所有必要配置文件都存在
echo 2. 完善缺失的文档
echo 3. 解决编译错误（如果有）
echo 4. 运行测试：mvn test
echo 5. 检查代码质量：mvn checkstyle:check
echo.
echo 更多信息请查看 docs\quality-score.md
pause