<template>
  <v-card class="table-container">
    <v-breadcrumbs :items="breadcrumbs" divider=">"></v-breadcrumbs>
    <v-btn prepend-icon="mdi-folder-plus" variant="tonal" style="margin-left: 15px;" color="blue" @click="dialog = true">
      Создать
    </v-btn>
    <v-btn prepend-icon="mdi-file-upload" variant="tonal" style="margin-left: 15px" color="" @click="uploadDialog = true">
      Загрузить
    </v-btn>

    <v-dialog v-model="dialog" persistent max-width="600px">
      <v-card>
        <v-card-title>
          <span class="headline">Создать папку</span>
        </v-card-title>
        <v-card-text>
          <v-container>
            <v-row>
              <v-col cols="12" sm="6" md="12">
                <v-text-field v-model="folderName" hide-details placeholder="Название папки" rounded density="compact"
                  variant="solo-filled" flat single-line required></v-text-field>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" text @click="closeDialog">Отмена</v-btn>
          <v-btn :disabled="!folderName" color="blue darken-1" text @click="createFolder">Создать</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="renameDialog" persistent max-width="600px">
      <v-card>
        <v-card-title>
          <span class="headline">Переименовать</span>
        </v-card-title>
        <v-card-text>
          <v-container>
            <v-row>
              <v-col cols="12" sm="6" md="12">
                <v-text-field v-model="newItemName" hide-details placeholder="Название папки" rounded density="compact"
                  variant="solo-filled" flat single-line required></v-text-field>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" text @click="closeRenameDialog">Отмена</v-btn>
          <v-btn :disabled="!newItemName" color="blue darken-1" text @click="updateFolderOrFile">Сохранить</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="uploadDialog" persistent max-width="600px">
      <v-card>
        <v-card-title>
          <span class="headline">Загрузить файл</span>
        </v-card-title>
        <v-card-text>
          <v-file-input v-model="file" label="Выберите файл или перетащите его сюда" chips show-size hide-details rounded
            density="compact" variant="solo-filled" flat single-line required :rules=sizeLimit></v-file-input>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" text @click="closeFileUploadDialog">Отмена</v-btn>
          <v-btn :disabled="!file" color="blue darken-1" text @click="saveFile">
            <v-progress-circular indeterminate color="white" size="20" width="2" v-if="uploading"></v-progress-circular>
            <span v-else>Сохранить</span>
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <div style="margin: 10px;">

      <v-data-table :headers="headers" :items="files" disable-pagination :items-per-page="-1" fixed-header
        v-if="files.length" >
        <template v-slot:item="{ item }">
          <tr @dblclick="rowClicked(item)" class="table-row">
            <td class="file-name-cell">
              <v-icon small class="mr-2">{{ getFileIcon(item) }}</v-icon>
              <span>{{ item.name }}</span>
            </td>
            <td class="fixed-width-cell">{{ formatBytes(item.size) }}</td>
            <td class="fixed-width-cell">
              <v-menu offset-y>
                <template v-slot:activator="{ props }">
                  <v-btn variant="text" color="grey" icon v-bind="props" @click.stop>
                    <v-icon>mdi-dots-vertical</v-icon>
                  </v-btn>
                </template>
                <v-list density="compact">
                  <v-list-item prepend-icon="mdi-tray-arrow-down" title="Скачать" value="download"
                    @click="downloadItem(item)" class="menu-icon-font">
                  </v-list-item>
                  <v-list-item prepend-icon="mdi-form-textbox" title="Переименовать" value="rename"
                    @click="editItem(item)" class="menu-icon-font">
                  </v-list-item>
                  <v-divider></v-divider>
                  <v-list-item prepend-icon="mdi-delete" title="Удалить" value="delete" @click="deleteItem(item)"
                    class="menu-icon-font">
                  </v-list-item>
                </v-list>
              </v-menu>
            </td>
          </tr>
        </template>
        <template #bottom></template>
      </v-data-table>
      <p v-else style="color: darkgray; margin: 20px;">
        Здесь пока ничего нет
      </p>
    </div>
  </v-card>
</template>
  
<style scoped>
.menu-icon-font :deep(.v-icon) {
  font-size: 20px;
}

.table-container {
  margin: 10px;
}

.file-name-cell {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100px;
}

.fixed-width-cell {
  width: 100px;
  /* Задайте ширину в соответствии с вашими потребностями */
}

.table-row:hover {
  background-color: #2B2B2B;
  /* Задайте цвет в соответствии с вашими потребностями */
}
</style>

<script>
import api from '@/api';
import FileDownload from 'js-file-download';

export default {
  data: () => ({
    renameDialog: false,
    selectedItem: null,
    newItemName: null,
    uploadDialog: false,
    file: null,
    uploading: false,
    breadcrumbsList: [],
    files: [],
    headers: [
      { title: 'Имя файла', key: 'name' },
      { title: 'Размер', key: 'size' },
      // { title: 'Дата создания', key: 'created_at' },
      { title: 'Действия', key: 'action', sortable: false },

    ],
    dialog: false,
    folderName: '',
    cuurentFolder: '',
    sizeLimit: [
      value => {
        return !value || !value.length || value[0].size < 500000000 || 'Avatar size should be less than 2 MB!'
      },
    ],
  }),
  computed: {
    breadcrumbs() {
      return this.breadcrumbsList;
    }
  },
  watch: {
    '$route': 'fetchData'
  },
  methods: {
    async updateFolderOrFile() {
      if (this.newItemName === this.selectedItem.name){
        this.newItemName = '';
          this.selectedItem = null;
          this.renameDialog = false;
        return;
      }

      const data = {
        name: this.newItemName,
        parent_id: this.cuurentFolder.id,
      };

      await api.put(this.selectedItem._links.self.href, data)
        .then(() => {
          this.newItemName = '';
          this.selectedItem = null;
          this.renameDialog = false;
          this.fetchData();
        });
    },
    async downloadItem(item) {
      await api.get(item._links.download.href, {
        responseType: 'blob'
      })
        .then(response => {
        if (item.is_folder){
          FileDownload(response.data, item.name + ".zip");
        }
        else{
          FileDownload(response.data, item.name);
        }
        });
    },
    uploadFile() {
      this.uploading = true;
      // Здесь ваш код для загрузки файла
      // Обновите uploadProgress в процессе загрузки
    },
    setBreadcrumbs(breadcrumbs) {
      this.breadcrumbsList = [{ title: 'Диск', disabled: false, href: '/drive' },];
      breadcrumbs.forEach(element => {
        this.breadcrumbsList.push({
          title: element.name,
          disabled: false,
          href: `/drive/${element.id}`,
        })
      });
      this.breadcrumbsList[this.breadcrumbsList.length - 1].disabled = true;
    },
    async fetchData() {
      if (this.$route.params.folderId) {
        // console.log(this.$route.params.folderId);
        await api.get(`/folders/${this.$route.params.folderId}`)
          .then(response => {
            this.cuurentFolder = response.data;
            // console.log(this.cuurentFolder);
          })
        await api.get(this.cuurentFolder._links.content.href)
          .then(response => {
            this.cuurentFolder = response.data;
            this.files = response.data.content;
            console.log(response);
          })
        // console.log(this.cuurentFolder._links.breadcrumbs.href);
        await api.get(this.cuurentFolder._links.breadcrumbs.href)
          .then(response => {
            // console.log(response.data);

            this.setBreadcrumbs(response.data);
          })
      }
      else {
        await api.get("/folders")
          .then(response => {
            this.cuurentFolder = response.data;
            console.log(this.cuurentFolder);
            // console.log(response.data._links.self);
            if (response.data._embedded == undefined) {
              this.files = [];
            }
            else {
              this.files = response.data._embedded.fileDtoResponseList;
            }

            this.setBreadcrumbs([])
          })
      }
    },
    formatBytes(bytes) {
      if (bytes == null) {
        return "—";
      }
      if (bytes == 0) {
        return "0 Б";
      }
      const units = ['Б', 'КБ', 'МБ', 'ГБ', 'ТБ', 'ПБ', 'ЭБ', 'ЗБ', 'ЙБ'];
      let power = Math.floor(Math.log(bytes) / Math.log(1024));
      power = Math.min(power, units.length - 1);
      bytes /= Math.pow(1024, power);
      return `${bytes.toFixed(1)} ${units[power]}`;
    },
    rowClicked(item) {
      if (item.is_folder) {
        this.$router.push(`/drive/${item.id}`);
      }
      // else {
      //   window.open(item._links.preview.href, '_blank');
      // }
    },
    editItem(item) {
      this.newItemName = item.name;

      this.selectedItem = item;
      this.renameDialog = true;
    },
    async deleteItem(item) {
      await api.delete(item._links.self.href);
      this.fetchData();
    },
    async createFolder() {
      const newFolder = {
        name: this.folderName,
      }
      console.log(this.cuurentFolder._links.self.href);
      await api.post(this.cuurentFolder._links.self.href, newFolder)
        .then(response => {
          // console.log(response.data);
          this.files.push(response.data)
        })
      this.dialog = false;
      this.folderName = '';
    },

    closeDialog() {
      this.dialog = false;
      this.folderName = '';
    },

    closeFileUploadDialog() {
      this.uploadDialog = false;
      this.file = '';
    },
    async saveFile() {

      // console.log(this.file.raw);
      const formData = new FormData();
      formData.append('file', this.file[0]);

      this.uploading = true;

      await api.post(this.cuurentFolder._links.upload_file.href, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        },
      })
        .then(response => {
          this.files.push(response.data);
        })

      this.uploading = false;
      this.uploadDialog = false;
      this.file = '';
    },
    closeRenameDialog() {
      this.newItemName = '';
      this.selectedItem = null;
      this.renameDialog = false;
    },
    getFileIcon(file) {
      if (file.is_folder) {
        return 'mdi-folder';
      }
      const extension = file.name.split('.').pop();
      switch (extension) {
        case 'txt':
          return 'mdi-note-text';
        case 'docx':
        case 'doc':
          return 'mdi-file-word';
        case 'xlsx':
        case 'xls':
          return 'mdi-file-excel';
        case 'pptx':
        case 'ppt':
          return 'mdi-file-powerpoint';
        case 'pdf':
          return 'mdi-file-pdf';
        case 'jpg':
        case 'png':
        case 'gif':
          return 'mdi-file-image';
        case 'mp4':
        case 'avi':
        case 'mov':
          return 'mdi-file-video';
        case 'mp3':
        case 'wav':
        case 'flac':
          return 'mdi-file-music';
        case 'zip':
        case 'rar':
        case 'tar':
        case 'gz':
          return 'mdi-zip-box';
        case 'csv':
          return 'mdi-file-delimited';
        case 'json':
          return 'mdi-code-json';
        case 'xml':
          return 'mdi-xml';
        case 'html':
          return 'mdi-language-html5';
        case 'css':
          return 'mdi-language-css3';
        case 'js':
          return 'mdi-language-javascript';
        case 'py':
          return 'mdi-language-python';
        case 'java':
          return 'mdi-language-java';
        default:
          return 'mdi-file';
      }
    },
  },
  async mounted() {
    await this.fetchData();
  }
}
</script>
  