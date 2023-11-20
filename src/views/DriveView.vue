<template>
    <v-card class="table-container">
        <v-breadcrumbs :items="breadcrumbs" divider=">"></v-breadcrumbs>
        <v-btn prepend-icon="mdi-plus" variant="tonal" style="margin: 1%;" color="blue">
            Создать
        </v-btn>
        <v-btn prepend-icon="mdi-upload" variant="tonal" style="margin: 1%;" color="">
            Загрузить
        </v-btn>

        <v-data-table :headers="headers" :items="files" disable-pagination :items-per-page="-1" class="elevation-1" v-if="files.length">
  <template v-slot:item="{ item }">
    <tr @click="rowClicked(item)">
        <td>{{ item.name }}</td>
      <td>{{ formatBytes(item.size) }}</td>
      <td>
        <v-icon small class="mr-2" @click.stop="editItem(item)">
          mdi-pencil
        </v-icon>
        <v-icon small @click.stop="deleteItem(item)">
          mdi-delete
        </v-icon>
      </td>
    </tr>
  </template>
  <template #bottom></template>
</v-data-table>
<h1 v-else>
        asd
    </h1>
    </v-card>

  </template>
  
  <style scoped>
  .table-container {
    margin: 10px;
  }
  
  /* .rounded-table {
    border-radius: 20px;
  } */
  </style>

  <script>
import api from '@/api';

  export default {
    data: () => ({
        items: [
      { title: 'Диск', disabled: false, href: '/drive' },
    ],
        files: [],
      headers: [
        { title: 'Имя файла', key: 'name' },
        { title: 'Размер', key: 'size' },
        // { title: 'Дата создания', key: 'created_at' },
        { title: 'Действия', key: 'action', sortable: false },

      ],
    }),
    computed :{
        breadcrumbs(){
            return this.items;
        }
    },
    methods: {
        formatBytes(bytes) {
        if (bytes == null){
            return "";
        }
        const units = ['Байт', 'КБ', 'МБ', 'ГБ', 'ТБ', 'ПБ', 'ЭБ', 'ЗБ', 'ЙБ'];
        let power = Math.floor(Math.log(bytes) / Math.log(1024));
        power = Math.min(power, units.length - 1);
        bytes /= Math.pow(1024, power);
        return `${bytes.toFixed(2)} ${units[power]}`;
        },
        rowClicked(item){
            console.log(item._links.content);
            api.get(item._links.content.href)
            .then(response => {
                this.files = response.data;
                this.items.push({
                    title: item.name,
                    disabled: false,
                    href: `/drive/${item.id}`,
                })
                this.$router.push(`/drive/${item.id}`);
            })
        },
      editItem(item) {
        // Реализуйте логику редактирования здесь
      },
      deleteItem(item) {
        // Реализуйте логику удаления здесь
      },
    },
    async mounted(){
        // axios.get('http://localhost:8081')
        //   .then(response => {
        //     this.files = response.data;
        //   })
        let temp;
        if (this.$route.params.folderId){
            await api.get(`/folders/${this.$route.params.folderId}`)
        .then(response => {
            temp = response.data;
            // console.log(temp);
        })
        await api.get(temp._links.content.href)
        .then(response => {
            this.files = response.data.content;
            // console.log(this.files);
        })
        console.log(temp._links.breadcrumbs.href);
        await api.get(temp._links.breadcrumbs.href)
        .then(response => {
            console.log(response.data);
            response.data.forEach(element => {
                this.items.push({
                    title: element.name,
                    disabled: false,
                    href: `/drive/${element.id}`,
                })
            });
        })
        }
        else{

            await api.get("/folders")
            .then(response => {
                this.files = response.data._embedded.fileDtoResponseList;
            })
        }
    }
  }
  </script>
  