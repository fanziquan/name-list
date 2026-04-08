@echo off
echo ============================================
echo 项目质量检查脚本
echo ============================================
echo.

echo [1/6] 检查项目结构...
if exist "pom.xml" (
    echo ✓ 找到Maven项目 (pom.xml)
) else if exist "package.json" (
    echo ✓ 找到Node.js项目 (package.json)
) else if exist "requirements.txt" (
    echo ✓ 找到Python项目 (requirements.txt)
) else (
    echo ⚠ 未识别到标准项目结构
)

echo.
echo [2/6] 检查核心文档...
if exist "README.md" (
    echo ✓ README.md存在
) else (
    echo ✗ 缺少README.md
)

if exist "AGENTS.md" (
    echo ✓ AGENTS.md存在
) else (
    echo ⚠ 缺少AGENTS.md (建议创建)
)

if exist "docs" (
    echo ✓ docs目录存在
) else (
    echo ⚠ 缺少docs目录 (建议创建)
)

echo.
echo [3/6] 检查构建配置...
if exist ".gitignore" (
    echo ✓ .gitignore存在
) else (
    echo ⚠ 缺少.gitignore
)

if exist "LICENSE" (
    echo ✓ LICENSE存在
) else (
    echo ⚠ 缺少LICENSE文件
)

echo.
echo [4/6] 检查代码质量工具...
if exist "checkstyle.xml" (
    echo ✓ Checkstyle配置存在
) else (
    echo ⚠ 缺少Checkstyle配置 (Java项目)
)

if exist ".eslintrc.js" (
    echo ✓ ESLint配置存在
) else (
    echo ⚠ 缺少ESLint配置 (JavaScript项目)
)

if exist ".pylintrc" (
    echo ✓ Pylint配置存在
) else (
    echo ⚠ 缺少Pylint配置 (Python项目)
)

echo.
echo [5/6] 运行基本构建检查...
if exist "pom.xml" (
    echo 尝试运行Maven编译检查...
    mvn compile -q
    if %errorlevel% equ 0 (
        echo ✓ Maven编译成功
    ) else (
        echo ✗ Maven编译失败
    )
) else if exist "package.json" (
    echo 尝试运行npm安装检查...
    npm install --silent
    if %errorlevel% equ 0 (
        echo ✓ npm安装成功
    ) else (
        echo ✗ npm安装失败
    )
)

echo.
echo [6/6] 总结...
echo ============================================
echo 检查完成！
echo.
echo 建议改进项：
echo 1. 确保所有核心文档完整 (README.md, AGENTS.md, docs/)
echo 2. 配置适当的代码质量工具
echo 3. 添加自动化测试
echo 4. 设置CI/CD流水线
echo ============================================