import {Action, getModule, Module, Mutation, VuexModule} from 'vuex-module-decorators'
import {getUserInfo, login} from '@/api/users'
import {getToken, removeToken, setToken} from '@/utils/cookies'
import store from '@/store'

export interface IUserState {
  token: string
  name: string
  avatar: string
  introduction: string
  roles: string[]
}

@Module({dynamic: true, store, name: 'user'})
class User extends VuexModule implements IUserState {
  public token = getToken() || ''
  public name = ''
  public avatar = ''
  public introduction = ''
  public roles: string[] = []

  @Mutation
  private SET_TOKEN(token: string) {
    this.token = token
  }

  @Mutation
  private SET_NAME(name: string) {
    this.name = name
  }

  @Mutation
  private SET_AVATAR(avatar: string) {
    this.avatar = avatar
  }

  @Mutation
  private SET_INTRODUCTION(introduction: string) {
    this.introduction = introduction
  }

  @Mutation
  private SET_ROLES(roles: string[]) {
    this.roles = roles
  }

  @Action
  public async Login(userInfo: { username: string, password: string }) {
    let {username, password} = userInfo
    username = username.trim()
    const data: any = await login({
      loginName: username,
      password: password,
    })
    if (data.token) {
      localStorage.setItem('adminId', data.adminId);
      setToken(data.token)
      this.SET_TOKEN(data.token)
      return {success: true}
    } else {
      return {success: false}
    }
  }

  @Action
  public ResetToken() {
    removeToken()
    this.SET_TOKEN('')
    this.SET_ROLES([])
  }

  @Action
  public async GetUserInfo() {
    // return;
    if (this.token === '') {
      throw Error('GetUserInfo: token is undefined!')
    }
    const {data} = await getUserInfo({ /* Your params here */})
    if (!data) {
      throw Error('Verification failed, please Login again.')
    }
    const {roles, name, avatar, introduction} = data.user
    // roles must be a non-empty array
    if (!roles || roles.length <= 0) {
      throw Error('GetUserInfo: roles must be a non-null array!')
    }
    this.SET_ROLES(roles)
    this.SET_NAME(name)
    this.SET_AVATAR(avatar)
    this.SET_INTRODUCTION(introduction)
  }

  @Action
  public async LogOut() {
    if (this.token === '') {
      throw Error('LogOut: token is undefined!')
    }
    // await logout()
    removeToken()
    this.SET_TOKEN('')
    this.SET_ROLES([])
  }
}

export const UserModule = getModule(User)
