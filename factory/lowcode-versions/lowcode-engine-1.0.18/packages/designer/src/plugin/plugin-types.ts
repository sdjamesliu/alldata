import Logger from 'zen-logger';
import {
  IPublicApiHotkey,
  IPublicApiProject,
  IPublicApiSkeleton,
  IPublicApiSetters,
  IPublicApiMaterial,
  IPublicApiEvent,
  IPublicApiCommon,
  CompositeObject,
  ComponentAction,
  MetadataTransducer,
} from '@alilc/lowcode-types';
import { EngineConfig } from '@alilc/lowcode-editor-core';
import { Setters } from '../types';

export type PreferenceValueType = string | number | boolean;

export interface ILowCodePluginPreferenceDeclarationProperty {
  // shape like 'name' or 'group.name' or 'group.subGroup.name'
  key: string;
  // must have either one of description & markdownDescription
  description: string;
  // value in 'number', 'string', 'boolean'
  type: string;
  // default value
  // NOTE! this is only used in configuration UI, won`t affect runtime
  default?: PreferenceValueType;
  // only works when type === 'string', default value false
  useMultipleLineTextInput?: boolean;
  // enum values, only works when type === 'string'
  enum?: any[];
  // descriptions for enum values
  enumDescriptions?: string[];
  // message that describing deprecation of this property
  deprecationMessage?: string;
}

/**
 * declaration of plugin`s preference
 * when strictPluginMode === true， only declared preference can be obtained from inside plugin.
 *
 * @export
 * @interface ILowCodePluginPreferenceDeclaration
 */
export interface ILowCodePluginPreferenceDeclaration {
  // this will be displayed on configuration UI, can be plugin name
  title: string;
  properties: ILowCodePluginPreferenceDeclarationProperty[];
}

export type PluginPreference = Map<string, Record<string, PreferenceValueType>>;

export interface ILowCodePluginConfig {
  dep?: string | string[];
  init?(): void;
  destroy?(): void;
  exports?(): any;
}

export interface ILowCodePluginConfigMetaEngineConfig {
  lowcodeEngine?: string;
}
export interface ILowCodePluginConfigMeta {
  preferenceDeclaration?: ILowCodePluginPreferenceDeclaration;
  // 依赖插件名
  dependencies?: string[];
  engines?: ILowCodePluginConfigMetaEngineConfig;
}

export interface ILowCodePluginCore {
  name: string;
  dep: string[];
  disabled: boolean;
  config: ILowCodePluginConfig;
  logger: Logger;
  on(event: string | symbol, listener: (...args: any[]) => void): any;
  emit(event: string | symbol, ...args: any[]): boolean;
  removeAllListeners(event?: string | symbol): this;
  init(forceInit?: boolean): void;
  isInited(): boolean;
  destroy(): void;
  toProxy(): any;
  setDisabled(flag: boolean): void;
}

interface ILowCodePluginExportsAccessor {
  [propName: string]: any;
}

export type ILowCodePlugin = ILowCodePluginCore & ILowCodePluginExportsAccessor;

export interface IDesignerCabin {
  registerMetadataTransducer: (transducer: MetadataTransducer, level: number, id?: string) => void;
  addBuiltinComponentAction: (action: ComponentAction) => void;
  removeBuiltinComponentAction: (actionName: string) => void;
}

export interface IPluginPreferenceMananger {
  // eslint-disable-next-line max-len
  getPreferenceValue: (
    key: string,
    defaultValue?: PreferenceValueType,
  ) => PreferenceValueType | undefined;
}

export interface ILowCodePluginContext {
  get skeleton(): IPublicApiSkeleton;
  get hotkey(): IPublicApiHotkey;
  get setters(): IPublicApiSetters;
  get config(): EngineConfig;
  get material(): IPublicApiMaterial;
  get event(): IPublicApiEvent;
  get project(): IPublicApiProject;
  get common(): IPublicApiCommon;
  logger: Logger;
  plugins: ILowCodePluginManager;
  preference: IPluginPreferenceMananger;
}
export interface ILowCodePluginContextPrivate {
  set hotkey(hotkey: IPublicApiHotkey);
  set project(project: IPublicApiProject);
  set skeleton(skeleton: IPublicApiSkeleton);
  set setters(setters: Setters);
  set material(material: IPublicApiMaterial);
  set event(event: IPublicApiEvent);
  set config(config: EngineConfig);
  set common(common: IPublicApiCommon);
}
export interface ILowCodePluginContextApiAssembler {
  assembleApis: (context: ILowCodePluginContextPrivate) => void;
}


interface ILowCodePluginManagerPluginAccessor {
  [pluginName: string]: ILowCodePlugin | any;
}

export interface ILowCodePluginManagerCore {
  register(
    pluginConfigCreator: (ctx: ILowCodePluginContext, pluginOptions?: any) => ILowCodePluginConfig,
    pluginOptions?: any,
    options?: CompositeObject,
  ): Promise<void>;
  init(pluginPreference?: Map<string, Record<string, PreferenceValueType>>): Promise<void>;
  get(pluginName: string): ILowCodePlugin | undefined;
  getAll(): ILowCodePlugin[];
  has(pluginName: string): boolean;
  delete(pluginName: string): any;
  setDisabled(pluginName: string, flag: boolean): void;
  dispose(): void;
}

export type ILowCodePluginManager = ILowCodePluginManagerCore & ILowCodePluginManagerPluginAccessor;

export function isLowCodeRegisterOptions(opts: any): opts is ILowCodeRegisterOptions {
  return opts && ('autoInit' in opts || 'override' in opts);
}

export interface ILowCodeRegisterOptions {
  /** Will enable plugin registered with auto-initialization immediately
   * other than plugin-manager init all plugins at certain time.
   * It is helpful when plugin register is later than plugin-manager initialization. */
  autoInit?: boolean;
  /** allow overriding existing plugin with same name when override === true */
  override?: boolean;
}

export interface IPluginContextOptions {
  pluginName: string;
}

export interface IPluginMetaDefinition {
  /** define dependencies which the plugin depends on */
  dependencies?: string[];
  /** specify which engine version is compatible with the plugin */
  engines?: {
    /** e.g. '^1.0.0' */
    lowcodeEngine?: string;
  };
}

interface IPluginConfigCreatorFn<T extends Record<string, any> = Record<string, any>> {
  (ctx: ILowCodePluginContext, pluginOptions?: T): ILowCodePluginConfig;
}

export type IPluginConfigCreator<T extends Record<string, any> = Record<string, any>> =
  IPluginConfigCreatorFn<T> & {
    pluginName: string;
    meta?: IPluginMetaDefinition;
  };
