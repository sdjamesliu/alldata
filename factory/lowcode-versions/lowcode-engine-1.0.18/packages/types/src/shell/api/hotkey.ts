import { Disposable } from '../../disposable';
import { HotkeyCallback } from '../../editor';


export interface IPublicApiHotkey {
  get callbacks(): any;

  /**
   * 绑定快捷键
   * @param combos 快捷键，格式如：['command + s'] 、['ctrl + shift + s'] 等
   * @param callback 回调函数
   * @param action
   * @returns
   */
  bind(combos: string[] | string, callback: HotkeyCallback, action?: string): Disposable;
}
