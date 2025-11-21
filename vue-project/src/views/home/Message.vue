<template>
    <el-card class="message-list">
      <!-- 搜索框 -->
      <el-input
        v-model="searchKeyword"
        placeholder="请输入标题或内容搜索"
        clearable
        @input="handleSearch"
        style="margin-bottom: 20px; width: 300px;"
      >
      <template #prepend>
        <el-button :icon="Search" />
      </template>
      </el-input>
      <!-- 消息表格 -->
      <div class="table-container">
        <el-table :data="filteredMessages" style="width: 100%" @sort-change="handleSortChange">
          <el-table-column prop="title" label="消息类型" width="200" align="center"/>
          <el-table-column prop="content" label="内容">
            <template #default="{ row }">
              {{ truncateContent(row.content) }}
            </template>
          </el-table-column>
          <el-table-column prop="time" label="收信时间" sortable="custom" width="180" />
          <el-table-column label="操作" width="180" align="center">
            <template #default="{ row }">
              <el-button
                class="readed"
                type="success"
                circle
                plain
                @click="markAsRead(row)"
                title="标为已读"
              >
                <el-icon><Check /></el-icon>
              </el-button>
              <el-button
                class="delete"
                type="danger"
                circle
                plain
                @click="deleteMessage(row)"
                title="删除"
              >
                <el-icon><Delete /></el-icon>
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
  
      <!-- 底部操作栏 -->
      <div class="footer">
        <!-- 每页行数选择 -->
        <div>
          <span>每页显示：</span>
          <el-select v-model="pageSize" @change="handlePageSizeChange" style="width: 100px;">
            <el-option label="5" :value="5" />
            <el-option label="10" :value="10" />
            <el-option label="20" :value="20" />
          </el-select>
        </div>
  
        <!-- 分页 -->
        <el-pagination
          background
          layout="prev, pager, next"
          :total="totalMessages"
          :page-size="pageSize"
          :current-page="currentPage"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
</template>
  
<script setup>
  import { ref, computed, onMounted } from 'vue';
  import { Check, Delete, Search } from '@element-plus/icons-vue'; // 引入图标
  import { ElMessage, ElMessageBox } from 'element-plus'

      // 模拟数据
      const messages = ref([
        { id: 1, title: '消息1', content: '这是第一条消息的内容，用于测试消息列表的显示效果。', time: '2023-10-01 10:00' },
        { id: 2, title: '消息2', content: '这是第二条消息的内容，用于测试消息列表的显示效果。', time: '2023-10-02 11:00' },
        { id: 3, title: '消息3', content: '这是第三条消息的内容，用于测试消息列表的显示效果。', time: '2023-10-03 12:00' },
        { id: 4, title: '消息4', content: '这是第四条消息的内容，用于测试消息列表的显示效果。', time: '2023-10-04 13:00' },
        { id: 5, title: '消息5', content: '这是第五条消息的内容，用于测试消息列表的显示效果。', time: '2023-10-05 14:00' },
        { id: 6, title: '消息6', content: '这是第六条消息的内容，用于测试消息列表的显示效果。', time: '2023-10-06 15:00' },
        { id: 7, title: '消息7', content: '这是第七条消息的内容，用于测试消息列表的显示效果。', time: '2023-10-07 16:00' },
        { id: 8, title: '消息8', content: '这是第八条消息的内容，用于测试消息列表的显示效果。', time: '2023-10-08 17:00' },
        { id: 9, title: '消息9', content: '这是第九条消息的内容，用于测试消息列表的显示效果。', time: '2023-10-09 18:00' },
        { id: 10, title: '消息10', content: '这是第十条消息的内容，用于测试消息列表的显示效果。', time: '2023-10-10 19:00' },
      ]);
  
      // 搜索关键词
      const searchKeyword = ref('');
  
      // 分页相关
      const pageSize = ref(5); // 每页显示 5 条
      const currentPage = ref(1); // 当前页码
  
      // 排序相关
      const sortOrder = ref(null); // 排序方式：null（默认）、'ascending'（升序）、'descending'（降序）
  
      // 过滤后的消息列表
      const filteredMessages = computed(() => {
        let filtered = messages.value.filter(
          (msg) =>
            msg.title.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
            msg.content.toLowerCase().includes(searchKeyword.value.toLowerCase())
        );
  
        // 排序逻辑
        if (sortOrder.value === 'ascending') {
          filtered.sort((a, b) => new Date(a.time) - new Date(b.time));
        } else if (sortOrder.value === 'descending') {
          filtered.sort((a, b) => new Date(b.time) - new Date(a.time));
        }
  
        // 分页逻辑
        const start = (currentPage.value - 1) * pageSize.value;
        const end = start + pageSize.value;
        return filtered.slice(start, end);
      });
  
      // 总消息数
      const totalMessages = computed(() => {
        return messages.value.filter(
          (msg) =>
            msg.title.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
            msg.content.toLowerCase().includes(searchKeyword.value.toLowerCase())
        ).length;
      });
  
      // 截取消息内容的前 20 个字
      const truncateContent = (content) => {
        return content.length > 40 ? content.slice(0, 40) + '...' : content;
      };
  
      // 处理搜索
      const handleSearch = () => {
        currentPage.value = 1; // 搜索时重置到第一页
      };
  
      // 处理分页变化
      const handlePageChange = (page) => {
        currentPage.value = page;
      };
  
      // 处理每页行数变化
      const handlePageSizeChange = (size) => {
        pageSize.value = size;
        currentPage.value = 1; // 切换每页行数时重置到第一页
      };
  
      // 处理排序变化
      const handleSortChange = ({ column, prop, order }) => {
        sortOrder.value = order;
      };

      const markAsRead = () => {

      };

      const deleteMessage = () => {
        ElMessageBox.confirm(
        '确认删除此条信息？删除后将不可找回！',
        '警告',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
        }
  )
      }

  
</script>
  
<style scoped>
.message-list {
  display: flex;
  flex-direction: column;
  height: 100%; /* 确保容器占满父容器高度 */
  margin: 20px auto;
  padding: 20px;
}

.table-container {
  flex: 1; /* 表格区域占据剩余空间 */
  overflow-y: auto; /* 如果内容过多，允许滚动 */
  margin-bottom: 20px; /* 与分页区域保持间距 */
}

.footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto; /* 将分页区域置底 */
}

.readed, .delete {
    margin: 0px 15px;
}
</style>