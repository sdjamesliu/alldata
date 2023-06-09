import { JSExpression } from '../value-type';

/**
 * @deprecated use same function from '@alilc/lowcode-utils' instead
 */
export function isJSExpression(data: any): data is JSExpression {
  return data && data.type === 'JSExpression' && data.extType !== 'function';
}
