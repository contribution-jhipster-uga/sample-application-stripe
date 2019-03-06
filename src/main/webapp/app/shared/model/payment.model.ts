import { Moment } from 'moment';
import { IUser } from 'app/core/user/user.model';

export interface IPayment {
    id?: number;
    date?: Moment;
    token?: string;
    currency?: string;
    amount?: number;
    description?: string;
    capture?: boolean;
    receipt?: any;
    user?: IUser;
}

export class Payment implements IPayment {
    constructor(
        public id?: number,
        public date?: Moment,
        public token?: string,
        public currency?: string,
        public amount?: number,
        public description?: string,
        public capture?: boolean,
        public receipt?: any,
        public user?: IUser
    ) {
        this.capture = this.capture || false;
    }
}
