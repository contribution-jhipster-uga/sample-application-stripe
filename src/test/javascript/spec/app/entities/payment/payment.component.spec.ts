/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { StripeTestModule } from '../../../test.module';
import { PaymentComponent } from 'app/entities/payment/payment.component';
import { PaymentService } from 'app/entities/payment/payment.service';
import { Payment } from 'app/shared/model/payment.model';

describe('Component Tests', () => {
    describe('Payment Management Component', () => {
        let comp: PaymentComponent;
        let fixture: ComponentFixture<PaymentComponent>;
        let service: PaymentService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [StripeTestModule],
                declarations: [PaymentComponent],
                providers: []
            })
                .overrideTemplate(PaymentComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(PaymentComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(PaymentService);
        });

        it('Should call load all on init', () => {
            // GIVEN
            const headers = new HttpHeaders().append('link', 'link;link');
            spyOn(service, 'query').and.returnValue(
                of(
                    new HttpResponse({
                        body: [new Payment(123)],
                        headers
                    })
                )
            );

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.query).toHaveBeenCalled();
            expect(comp.payments[0]).toEqual(jasmine.objectContaining({ id: 123 }));
        });
    });
});
