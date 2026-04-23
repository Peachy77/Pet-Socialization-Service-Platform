module.exports = {
  preset: '@vue/cli-plugin-unit-jest',

  // 收集覆盖率
  collectCoverage: true,
  
  // 覆盖率报告输出目录
  coverageDirectory: 'coverage',
  
  // 覆盖率报告格式
  coverageReporters: ['lcov', 'text', 'html', 'json'],

}
