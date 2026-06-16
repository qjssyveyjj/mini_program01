<template>
  <div class="dashboard">
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="12">
        <el-card shadow="hover">
          <div class="stat">
            <div class="stat-value">{{ totalUsers }}</div>
            <div class="stat-label">注册用户总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <div class="stat">
            <div class="stat-value">{{ totalHealthRecords }}</div>
            <div class="stat-label">健康记录总数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>近 7 天注册用户趋势</template>
          <ChartComponent :option="lineChartOptions" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>用户来源分布</template>
          <ChartComponent :option="pieChartOptions" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import ChartComponent from '@/components/ChartComponent.vue'

const lineChartOptions = ref({})
const pieChartOptions = ref({})
const totalUsers = ref(0)
const totalHealthRecords = ref(0)

onMounted(async () => {
  try {
    const data = await request.get('/stats/users')
    const { dates, userCounts, registrationSources } = data
    totalUsers.value = data.totalUsers ?? 0
    totalHealthRecords.value = data.totalHealthRecords ?? 0

    lineChartOptions.value = {
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: dates },
      yAxis: { type: 'value' },
      series: [
        {
          name: '注册用户',
          type: 'line',
          smooth: true,
          data: userCounts,
          itemStyle: { color: '#42A5F5' },
          areaStyle: { color: 'rgba(66,165,245,0.2)' }
        }
      ]
    }

    pieChartOptions.value = {
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      color: ['#66BB6A', '#FFA726'],
      series: [
        {
          name: '用户来源',
          type: 'pie',
          radius: '60%',
          data: (registrationSources || []).map((item) => ({
            name: item.source,
            value: item.count
          }))
        }
      ]
    }
  } catch (e) {
    // 错误已由拦截器提示
  }
})
</script>

<style scoped>
.stat-cards {
  margin-bottom: 20px;
}
.stat {
  text-align: center;
  padding: 10px 0;
}
.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: var(--primary-color);
}
.stat-label {
  color: #888;
  margin-top: 8px;
}
</style>
