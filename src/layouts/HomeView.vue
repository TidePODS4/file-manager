<template>
  <v-app>
    <v-app-bar app class="d-flex align-center">
      <v-app-bar-title class="ml-5">
        <router-link to="/" style="text-decoration: none; color: inherit;">
          Мое приложение
        </router-link>
      </v-app-bar-title>

      <v-text-field hide-details placeholder="Поиск" prepend-inner-icon="mdi-magnify" rounded density="compact"
        variant="solo-filled" flat single-line style="max-width: 400px;" v-model="searchText"
        ></v-text-field>

      <!-- <v-autocomplete v-model="searchText" :items="searchitems" auto-select-first density="compact" style="max-width: 400px; margin-top: 22px;"
        item-props menu-icon="" placeholder="Поиск" prepend-inner-icon="mdi-magnify" rounded theme="dark"
        variant="solo-filled" flat single-line @update:search="search" hide-no-data></v-autocomplete> -->
        

      <v-spacer />

      <v-btn icon v-if="isAuthenticated">
        <v-icon>mdi-cog</v-icon>
      </v-btn>
      <v-menu v-if="isAuthenticated" rounded>
        <template v-slot:activator="{ props }">
          <v-btn icon v-bind="props">
            <v-avatar :color="avatarColor" size="40">
              <span class="text-h6" style="font-weight: 400;">{{ userInitials }}</span>
            </v-avatar>
          </v-btn>
        </template>
        <v-card>
          <v-card-text>
            <div class="mx-auto text-center">
              <v-avatar :color="avatarColor">
                <span class="text-h6" style="font-weight: 400;">{{ userInitials }}</span>
              </v-avatar>
              <h3 style="margin-top: 5px;">{{ userName }}</h3>
              <p class="text-caption mt-1">
                {{ userEmail }}
              </p>
              <v-btn style="margin-top: 10px;" color="red" @click="logout">
                Выйти
              </v-btn>
            </div>
          </v-card-text>
        </v-card>

      </v-menu>
      <v-btn v-else color="blue" @click="login">
        Войти
      </v-btn>
    </v-app-bar>

    <v-navigation-drawer expand-on-hover rail>
      <v-list density="compact" nav>
        <v-list-item prepend-icon="mdi-folder" title="Мои файлы" value="myfiles" rounded="xl" :to="'drive'"></v-list-item>
      </v-list>
    </v-navigation-drawer>

    <v-main>
      <router-view />
    </v-main>
  </v-app>
</template>

<script>
import api from '@/api';
import keycloak from '@/plugins/keycloak';

export default {
  data() {
    return {
      userInitials: '',
      userName: '',
      userEmail: '',
      searchText: '',
      searchitems: [],
    };
  },
  computed: {
    isAuthenticated() {
      return keycloak.authenticated;
    },
    avatarColor() {
      return this.strToHslColor(keycloak.idTokenParsed.email);
    },
  },
  methods: {
    async search() {
      if (!this.isAuthenticated ){
        return;
      }
      await api.get("http://localhost:8081/search", {
        params:{
          q: this.searchText,
        }
      })
      .then(response => {
        console.log(response.data._embedded.fileDtoResponseList);
        this.searchitems = response.data._embedded.fileDtoResponseList;
      });
    },
    login() {
      keycloak.login();
    },
    logout() {
      keycloak.logout({ redirectUri: window.location.origin });
    },
    strToHslColor(str, s = 30, l = 80) {
      var hash = 0;
      for (var i = 0; i < str.length; i++) {
        hash = str.charCodeAt(i) + ((hash << 5) - hash);
      }

      var h = hash % 360;
      return 'hsl(' + h + ', ' + s + '%, ' + l + '%)';
    },
  },
  mounted() {
    if (keycloak.authenticated) {
      this.userInitials = keycloak.idTokenParsed.given_name.charAt(0).toUpperCase() +
        keycloak.idTokenParsed.family_name.charAt(0).toUpperCase();
      this.userName = keycloak.idTokenParsed.name;
      this.userEmail = keycloak.idTokenParsed.email;
    }
  },

};
</script>