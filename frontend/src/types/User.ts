import { reactive } from "vue";

export interface User {
  id?: string;
  username: string;
  firstname?: string;
  lastname?: string;
  role?: string;
}

export const globalState = reactive({
  user: {
    id: null as string | null,
    username: null as string | null,
    firstname: null as string | null,
    lastname: null as string | null,
    role: null as string | null,
  },
  setUser(user: Partial<typeof globalState.user>) {
    Object.assign(this.user, user);
  },
  clearUser() {
    this.user = {
      id: null,
      username: null,
      firstname: null,
      lastname: null,
      role: null,
    };
  },
});
