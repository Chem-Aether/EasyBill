<template>
  <div class="travel-admin">
    <header class="mobile-header">
      <button class="menu-toggle" aria-label="打开导航" @click="navOpen = true"><i></i><i></i><i></i></button>
      <div><strong>旅行管理</strong><span>{{ currentSection }}</span></div>
      <router-link to="/travel">地图</router-link>
    </header>
    <div v-if="navOpen" class="nav-backdrop" @click="navOpen = false"></div>
    <aside class="admin-nav" :class="{ open: navOpen }">
      <div class="brand-block">
        <div class="brand-mark">旅</div>
        <div><strong>TRAVEL LOG</strong><span>个人旅行档案</span></div>
        <button class="nav-close" aria-label="关闭导航" @click="navOpen = false">×</button>
      </div>
      <nav aria-label="旅行管理导航">
        <p>数据管理</p>
        <router-link to="/travel/admin/trainTicket"><b>铁</b><span>铁路行程<small>车票与途经站</small></span></router-link>
        <router-link to="/travel/admin/flightTicket"><b>航</b><span>航空行程<small>航班与里程记录</small></span></router-link>
        <router-link to="/travel/admin/footprint"><b>迹</b><span>旅行足迹<small>地点与探索记录</small></span></router-link>
      </nav>
      <div class="nav-footer">
        <button class="export-button" :disabled="exporting" @click="exportAll"><span>{{ exporting ? '正在生成...' : '导出全部数据' }}</span><b>↓</b></button>
        <div class="nav-links"><router-link to="/home">返回首页</router-link><router-link to="/travel">查看地图</router-link></div>
      </div>
    </aside>
    <main class="admin-content"><div class="content-frame"><router-view /></div></main>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { exportTravelData } from '@/modules/travel/apis/travel.js'

const route = useRoute()
const navOpen = ref(false)
const exporting = ref(false)
const currentSection = computed(() => route.path.includes('trainTicket') ? '铁路行程' : route.path.includes('flightTicket') ? '航空行程' : '旅行足迹')
watch(() => route.path, () => { navOpen.value = false })

async function exportAll() {
  exporting.value = true
  try {
    const response = await exportTravelData()
    const blob = response.data
    if (!(blob instanceof Blob) || blob.size === 0) throw new Error('导出文件为空')
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `travel-data-${new Date().toISOString().slice(0, 10)}.xlsx`
    document.body.appendChild(link)
    link.click()
    link.remove()
    setTimeout(() => URL.revokeObjectURL(url), 1000)
    ElMessage.success('旅行数据已导出')
  } catch { ElMessage.error('导出失败') }
  finally { exporting.value = false }
}
</script>

<style scoped>
.travel-admin { --ink:#17242c; --muted:#6d7d84; --line:#dce5e7; --paper:#fff; --accent:#087f8c; min-height:100vh; background:#eef2f2; color:var(--ink); }
.admin-nav { position:fixed; z-index:30; inset:0 auto 0 0; display:flex; width:250px; flex-direction:column; box-sizing:border-box; padding:24px 16px 18px; background:#142329; color:#eaf3f3; box-shadow:12px 0 32px rgba(20,35,41,.12); }
.brand-block { display:flex; align-items:center; gap:12px; padding:0 8px 28px; }.brand-mark { display:grid; width:40px; height:40px; flex:0 0 auto; place-items:center; border-radius:6px; background:#e2583e; color:#fff; font-weight:800; }
.brand-block strong,.brand-block span { display:block; letter-spacing:0; }.brand-block strong { font-size:14px; }.brand-block span { margin-top:3px; color:#89a1a7; font-size:11px; }.nav-close { display:none; margin-left:auto; border:0; background:transparent; color:#a9bdc1; font-size:25px; }
nav p { margin:0 10px 9px; color:#708a90; font-size:10px; font-weight:700; } nav a { display:flex; align-items:center; gap:12px; min-height:58px; margin-bottom:5px; padding:0 11px; border-radius:6px; color:#b8c9cc; text-decoration:none; transition:background-color .18s,color .18s; }
nav a:hover { background:rgba(255,255,255,.055); color:#fff; } nav a.router-link-active { background:#edf6f5; color:#18343a; } nav a>b { display:grid; width:29px; height:29px; flex:0 0 auto; place-items:center; border:1px solid currentColor; border-radius:50%; font-size:11px; }
nav a span,nav a small { display:block; } nav a span { font-size:13px; font-weight:650; } nav a small { margin-top:3px; color:#708a90; font-size:10px; font-weight:400; }
.nav-footer { margin-top:auto; }.export-button { display:flex; width:100%; height:42px; align-items:center; justify-content:space-between; padding:0 14px; border:1px solid #3b565c; border-radius:5px; background:transparent; color:#d7e5e7; cursor:pointer; }.export-button:hover { border-color:#68b9bd; background:rgba(104,185,189,.08); }.export-button:disabled { opacity:.55; }
.nav-links { display:flex; justify-content:space-between; padding:18px 4px 0; }.nav-links a { color:#789097; font-size:11px; text-decoration:none; }.nav-links a:hover { color:#fff; }
.admin-content { min-height:100vh; margin-left:250px; box-sizing:border-box; padding:34px clamp(24px,4vw,64px); }.content-frame { width:min(1280px,100%); margin:0 auto; }.mobile-header,.nav-backdrop { display:none; }
.content-frame :deep(.page-title),.content-frame :deep(h1) { margin:0 0 22px; color:#17242c; font-size:25px; font-weight:750; }
.content-frame :deep(.query-card),.content-frame :deep(.train-card),.content-frame :deep(.flight-card) { border:1px solid var(--line); border-radius:7px; background:var(--paper); box-shadow:0 5px 18px rgba(35,58,64,.055); }
.content-frame :deep(.query-card) { margin-bottom:18px; }.content-frame :deep(.el-card__body) { padding:20px; }.content-frame :deep(.tool-bar) { display:flex; gap:10px; margin:0 0 18px; }
.content-frame :deep(.el-button) { border-radius:5px; }.content-frame :deep(.el-button--primary:not(.is-link)) { border-color:var(--accent); background:var(--accent); }.content-frame :deep(.el-button.is-link) { border-color:transparent; background:transparent; box-shadow:none; }.content-frame :deep(.el-input__wrapper),.content-frame :deep(.el-select__wrapper),.content-frame :deep(.el-textarea__inner) { border-radius:5px; box-shadow:0 0 0 1px #d8e2e4 inset; }
.content-frame :deep(.el-table) { overflow:hidden; border:1px solid var(--line); border-radius:7px; --el-table-header-bg-color:#f3f7f7; --el-table-row-hover-bg-color:#f3fafa; }.content-frame :deep(.el-table th.el-table__cell) { height:48px; color:#52656b; font-size:12px; }.content-frame :deep(.el-table td.el-table__cell) { height:50px; }
.content-frame :deep(.el-dialog) { border-radius:8px; }.content-frame :deep(.el-dialog__header) { padding:20px 24px 14px; border-bottom:1px solid #e7eded; }.content-frame :deep(.el-dialog__body) { padding:20px 24px; }
@media (max-width:760px) {
  .mobile-header { position:fixed; z-index:20; inset:0 0 auto; display:grid; height:58px; grid-template-columns:42px 1fr 42px; align-items:center; padding:0 12px; border-bottom:1px solid #dbe5e6; background:rgba(250,252,252,.94); backdrop-filter:blur(14px); }.mobile-header div strong,.mobile-header div span { display:block; }.mobile-header div strong { font-size:14px; }.mobile-header div span { margin-top:1px; color:#7a8d92; font-size:10px; }.mobile-header>a { color:#087f8c; font-size:12px; text-align:right; text-decoration:none; }
  .menu-toggle { display:flex; width:34px; height:34px; flex-direction:column; justify-content:center; gap:4px; padding:0 7px; border:0; background:transparent; }.menu-toggle i { height:2px; background:#263a40; }.admin-nav { width:min(286px,86vw); transform:translateX(-105%); transition:transform .22s ease; }.admin-nav.open { transform:translateX(0); }.nav-close { display:block; }.nav-backdrop { position:fixed; z-index:25; inset:0; display:block; background:rgba(7,17,22,.48); backdrop-filter:blur(2px); }
  .admin-content { margin-left:0; padding:82px 12px 28px; }.content-frame :deep(.page-title),.content-frame :deep(h1) { margin-bottom:16px; font-size:21px; }.content-frame :deep(.query-card .el-form) { display:grid; grid-template-columns:1fr; }.content-frame :deep(.query-card .el-form-item) { width:100%; margin-right:0; }.content-frame :deep(.query-card .el-input),.content-frame :deep(.query-card .el-select),.content-frame :deep(.query-card .el-date-editor) { width:100%!important; }
  .content-frame :deep(.el-dialog) { width:calc(100vw - 20px)!important; margin:10px auto; }.content-frame :deep(.el-dialog__body) { max-height:calc(100vh - 145px); overflow-y:auto; padding:16px; }
}
</style>
