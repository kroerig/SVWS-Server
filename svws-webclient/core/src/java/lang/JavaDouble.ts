import { JavaObject } from './JavaObject';
import { NullPointerException } from './NullPointerException';
import { NumberFormatException } from './NumberFormatException';

export class JavaDouble extends JavaObject {

	public static MAX_VALUE : number = Number.MAX_VALUE;
	public static MIN_VALUE : number = Number.MIN_VALUE;
	public static SIZE : number = 64;
	public static BYTES : number = 8;

	public static parseDouble(s : string | null) : number {
		if (s === null)
			throw new NullPointerException();
		const a : number = parseFloat(s);
		if (Number.isNaN(a) || (a < this.MIN_VALUE) || (a > this.MAX_VALUE))
			throw new NumberFormatException();
		return a;
	}

	public static compare(a : number, b : number) {
		return a === b ? 0 : (a < b) ? -1 : 1;
	}

	transpilerCanonicalName(): string {
		return 'java.lang.Double';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return [
			'java.lang.Double',
			'java.lang.Number',
			'java.lang.Object',
			'java.lang.Comparable',
			'java.lang.Serializable'
		].includes(name);
	}

}


export function cast_java_lang_Double(obj : unknown) : number | null {
	return obj as number | null;
}
